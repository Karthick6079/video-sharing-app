package com.karthick.videosharingapp.service;

import com.karthick.videosharingapp.domain.dto.*;
import com.karthick.videosharingapp.entity.*;
import com.karthick.videosharingapp.exceptions.BusinessException;
import com.karthick.videosharingapp.exceptions.FileSizeExceededException;
import com.karthick.videosharingapp.interfaces.MultiPartUploadService;
import com.karthick.videosharingapp.interfaces.RecommendationService;
import com.karthick.videosharingapp.repository.*;
import com.karthick.videosharingapp.servicelogic.RecommendationServiceFactory;
import com.karthick.videosharingapp.servicelogic.VideoServiceLogic;
import com.karthick.videosharingapp.util.MapperUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final S3Service s3Service;

    private final VideoRepository videoRepo;

    private final MapperUtil mapperUtil;

    private final VideoServiceLogic videoServiceLogic;

    private final UserService userService;

    private final VideoWatchRepository videoWatchRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final MongoClient mongoClient;

    private final VideoWatchService videoWatchService;

    private final VideoLikeRepository videoLikeRepository;

    private final RecommendationRefreshQueue recommendationRefreshQueue;

    private final double MB_MULTIPLIER = 0.00000095367432;

    private final String RESPONSE_TO_FRONTEND_LOG = "The response sent back to frontend";

    private final RecommendationServiceFactory recommendationServiceFactory;

    private final MultiPartUploadService multiPartUploadService;

    private final VideoRepositoryCustomImpl videoRepositoryCustom;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String allowedVideoFileSize;


    public UploadVideoResponse uploadFile(MultipartFile file) {
        logger.info("Validating video file before it uploaded to AWS S3");
        validateUploadedFileSize(file, allowedVideoFileSize);
        String url = s3Service.uploadFile(file);
        logger.info("Video file uploaded successfully!");
        Video video = new Video();
        video.setVideoUrl(url);
        logger.info("The video cloud url stored in database");
        Video savedVideo = videoRepo.save(video);
        logger.info("The video cloud url sent back to frontend");
        logger.info(RESPONSE_TO_FRONTEND_LOG);
        return new UploadVideoResponse(url, savedVideo.getId());
    }

    public UploadVideoResponse uploadThumbnail(MultipartFile file, String videoId) {
        logger.info("Validating video thumbnail file before it uploaded to AWS S3");
        validateUploadedFileSize(file, allowedVideoFileSize);
        String url = s3Service.uploadFile(file);

        logger.info("The video information fetched from database to update thumbnail url");
        Video savedVideo = findVideoById(videoId)
                .orElseThrow(() -> new BusinessException("Cannot find video by Id" + videoId));

        savedVideo.setThumbnailUrl(url);
        logger.info("The updated video entity stored in database");
        videoRepo.save(savedVideo);
        logger.info(RESPONSE_TO_FRONTEND_LOG);
        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public void validateUploadedFileSize(MultipartFile file, String allowedSize) {
        if (file == null) {
            logger.error("Uploaded file is empty!", new BusinessException("Uploaded file is empty!"));
            throw new BusinessException("Uploaded file is empty!");
        }
        double actualSize = file.getSize() * MB_MULTIPLIER;

        double allowedSizeDouble = Double.parseDouble(allowedSize.substring(0, allowedSize.length() - 2));

        if (actualSize > allowedSizeDouble) {
            String message = "The " + file.getOriginalFilename() + " exceeded the application's allowed size = " + allowedSize;
            logger.error(message, new FileSizeExceededException(message));
            throw new FileSizeExceededException(message);
        }
    }

    public VideoDTO editVideoMetaData(VideoDTO videoDto) {
        logger.info("Updating Video metadata..");
        logger.info("Fetching existing video entity from database");
        Video savedVideo = getVideoFromDB(videoDto.getId());

        User currerntUser = userService.getCurrentUser();
        savedVideo.setStatus(videoDto.getStatus());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setCreatedAt(Instant.now());
        savedVideo.setPublishedAt(Instant.now());
        savedVideo.setUserId(currerntUser.getId());

//        if(videoDto.getThumbnailUrl() == null || videoDto.getThumbnailUrl().isEmpty())
//            multiPartUploadService.generateThumbnail(savedVideo);
//        savedVideo.setUser(currerntUser);

        //update video url to video dto
        videoDto.setVideoUrl(savedVideo.getVideoUrl());
        logger.info("Saving the updated video metadata to database");

        videoRepo.save(savedVideo);

        return videoDto;

    }

    public Optional<Video> findVideoById(String id) {
        return videoRepo.findById(id);
    }


    private Video getVideoFromDB(String id) {
        logger.info("Getting video from Database using video id");
        return findVideoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + id));
    }

    public VideoUserInfoDTO updateVideoWatchAndGetVideoDetails(String videoId) {
        // 1. Get requested video and update it views by 1
        logger.info("Get requested video and update it views by 1");
        MongoDatabase database = mongoClient.getDatabase("videoStreamingDB");
        updateViews(videoId, database);

        // 2. Get video and required user info from DB
        logger.info("Get video and required user info from DB");
        VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);
        VideoUserInfoDTO videoUserInfoDTO = mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
        User currentUser = userService.getCurrentUser();


        User videoUploadUser = userService.getUserById(videoUserInfoDTO.getUserId());

        // 3. Update watch history table
        logger.info("Update watch history table");
        if (currentUser != null){
            videoWatchService.updateWatchHistory(videoUserInfoDTO, database, currentUser);
            recommendationRefreshQueue.markUserForRefresh(currentUser.getId());
            boolean isCurrentUserSubscribed = subscriptionRepository.existsBySubscriberIdAndChannelId(currentUser.getId(), videoUploadUser.getId());
            videoUserInfoDTO.setCurrentUserSubscribedToChannel(isCurrentUserSubscribed);

            //update user liked and disliked information DTO
            videoRepositoryCustom.updatedUserVideoReactions(currentUser.getId(), videoUserInfoDTO.getVideoId(), videoUserInfoDTO);
        }

        // 4.Update video uploaded user subscribers count, is current user subscribed
        logger.info("Update video uploaded user subscribers count");
        Long channelSubscriberCount = subscriptionRepository.countByChannelId(videoUploadUser.getId());
        videoUserInfoDTO.setChannelSubscribersCount(new AtomicLong(channelSubscriberCount));

        logger.info("Mapped information VideoUserInfoDTO and response sent to frontend");
        return mapperUtil.map(videoUserInfoDTO, VideoUserInfoDTO.class);
    }

    private void updateViews(String videoId, MongoDatabase database) {
        logger.info("Video view count updating");
        MongoCollection<Document> collection = database.getCollection("videos");
        Bson filter = Filters.eq("_id", new ObjectId(videoId));
        Bson update = Updates.inc("views", 1);
        UpdateResult updateResult = collection.updateOne(filter, update);

        if (updateResult.getModifiedCount() == 1)
            logger.info("Video view count updated !");
    }


    public ReactionCountResponse likeVideo(String videoId, String userId) {

        ReactionCountResponse reactionCountResponse  = videoServiceLogic.likeVideo(videoId, userId);
        recommendationRefreshQueue.markUserForRefresh(userId);
        return reactionCountResponse;
    }

    public ReactionCountResponse dislikeVideo(String videoId, String userId) {
        ReactionCountResponse reactionCountResponse  = videoServiceLogic.dislikeVideo(videoId, userId);
        recommendationRefreshQueue.markUserForRefresh(userId);
        return reactionCountResponse;
    }




    public List<VideoUserInfoDTO> getRecommendationVideos(int page, int size) {

        return getVideos(page, size);
    }

    private List<VideoUserInfoDTO> getVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        boolean isUserLoggedIn = userService.isUserLoggedIn();
        RecommendationService recommendationService = recommendationServiceFactory
                .getRecommendationService(isUserLoggedIn);

        List<VideoUserInfoDTO> result = null;
        User currentUser = userService.getCurrentUser();
        if(isUserLoggedIn && currentUser != null){
            result =  recommendationService.getRecommendationVideos(currentUser.getId(), pageable);
        } else{
            result =  recommendationService.getRecommendationVideos(pageable);
        }


        return  result;
    }

    public List<VideoUserInfoDTO> getVideosAndUser(List<String> videos) {
        logger.info("Fetching Video and User information list of videos");
        return videos.stream().map(videoId -> {
            VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);
            return mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
        }).toList();
    }

    public List<VideoWatchDTO> fetchWatchedVideos(String userId, int page, int size) {
        // Skipping elements PageNumber * PageSize
        logger.info("Fetching user watched videos and filter and structured by database query");
        int skip = page * size;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        List<VideoWatch> videoWatchList = videoWatchRepository.getUserVideoWatchHistory(userId, skip, size);

        return mapperUtil.mapToList(videoWatchList, VideoWatchDTO.class);
    }

    public List<VideoUserInfoDTO> getShortVideo(int page, int size) {
        logger.info("Fetching short video from database");
        return getVideos(page, size);
    }

    public List<VideoUserInfoDTO> getSubscriptionVideos(int page, int size) {

        logger.info("Fetching videos from subscribed users");
        User user = userService.getCurrentUser();
        List<ChannelIdDTO> subscribedChannelIdDTOs = subscriptionRepository.findChannelIdBySubscriberId(user.getId());

        if (subscribedChannelIdDTOs.isEmpty())
            return null;
        List<String> subscribedChannelIds = subscribedChannelIdDTOs.stream().map(ChannelIdDTO::getChannelId).toList();
        PageRequest pageRequest = PageRequest.of(page, size);
        logger.info("Find latest video from user, database query fetched this result");

        List<String> videoIdList = videoRepo.findLatestVideoFromUsers(subscribedChannelIds, pageRequest);

        return getVideosAndUser(videoIdList);
    }

    public List<VideoLikeDTO> getLikedVideos(int page, int size) {

        User user = userService.getCurrentUser();

        List<VideoLike> videoLikes = videoLikeRepository.getLikedVideos(user.getId(), page * size, size);

        return mapperUtil.mapToList(videoLikes, VideoLikeDTO.class);
    }

    public List<VideoUserInfoDTO> getSearchedVideos(String searchText) {
        logger.info("Searching video based given input text using database query");
        List<VideoUserInfo> videoUserInfoList = videoRepo.findVideosBySearchText(searchText);
        Optional<List<VideoUserInfo>> videoUserInfoOptional = Optional.ofNullable(videoUserInfoList);
        logger.info("List of videos found: {} and it mapped to Video User Info DTO for frontend", videoUserInfoList == null? 0:videoUserInfoList.size());
        return mapperUtil.mapToList(videoUserInfoOptional.orElse(new ArrayList<>()), VideoUserInfoDTO.class);
    }

    public List<String> getTrendingTopics() {
        logger.info("Retrieving trending video topic by database query");
        List<String> topics = videoRepo.getTrendingTopics();
        return removeDuplicateTopics(topics);

    }

    private List<String> removeDuplicateTopics(List<String> topics) {

        List<String> distinctTopics = new ArrayList<>();

        if (topics != null) {
            distinctTopics = Arrays.stream(topics.toArray(new String[0])).distinct().toList();
            return distinctTopics;
        }

        return distinctTopics;
    }

    public List<VideoUserInfoDTO> getVideosByTopic(String topic) {
        logger.info("Retrieving video by given topic using database");
        List<VideoUserInfo> videoUserInfoList = videoRepo.findVideosByTopic(topic);
        Optional<List<VideoUserInfo>> videoUserInfoOptional = Optional.ofNullable(videoUserInfoList);
        logger.info("List of videos found: {} and it mapped to Video User Info DTO for frontend", videoUserInfoList == null? 0:videoUserInfoList.size());
        return mapperUtil.mapToList(videoUserInfoOptional.orElse(new ArrayList<>()), VideoUserInfoDTO.class);
    }
}
