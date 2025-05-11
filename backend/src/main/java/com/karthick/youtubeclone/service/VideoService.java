package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.controller.VideoController;
import com.karthick.youtubeclone.dto.*;
import com.karthick.youtubeclone.entity.*;
import com.karthick.youtubeclone.exceptions.BusinessException;
import com.karthick.youtubeclone.exceptions.FileSizeExceededException;
import com.karthick.youtubeclone.repository.LikeVideoRepo;
import com.karthick.youtubeclone.repository.VideoRepository;
import com.karthick.youtubeclone.repository.WatchedVideoRepo;
import com.karthick.youtubeclone.servicelogic.VideoServiceLogic;
import com.karthick.youtubeclone.util.MapperUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;

    private final VideoRepository videoRepo;

    private final MapperUtil mapperUtil;

    private final VideoServiceLogic videoServiceLogic;

    private final UserService userService;

    private final WatchedVideoRepo watchedVideoRepo;

    private final MongoClient mongoClient;

    private final WatchedVideoService watchedVideoService;

    private final LikeVideoRepo likeVideoRepo;

    private final double MB_MULTIPLIER = 0.00000095367432;

    private final String RESPONSE_TO_FRONTEND_LOG = "The response sent back to frontend";

    @Value("${spring.servlet.multipart.max-file-size}")
    private String allowedVideoFileSize;

    private final Logger logger = LoggerFactory.getLogger(VideoService.class);


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
        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setPublishedDateAndTime(LocalDateTime.now());
        savedVideo.setUserId(currerntUser.getId());
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

    public VideoUserInfoDTO getVideoUserInfo(String videoId) {
        // 1. Get requested video and update it viewCount by 1
        logger.info("Get requested video and update it viewCount by 1");
        MongoDatabase database = mongoClient.getDatabase("videoStreamingDB");
        updateViewCount(videoId, database);

        // 2. Get video and required user info from DB
        logger.info("Get video and required user info from DB");
        VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);
        User currentUser = userService.getCurrentUser();


        // 3. Update watch history table
        logger.info("Update watch history table");
        if (currentUser != null)
            watchedVideoService.updateWatchHistory(videoUserInfo, database, currentUser);

        // 4.Update video uploaded user subscribers count
        logger.info("Update video uploaded user subscribers count");
        User videoUploadUser = userService.getUserById(videoUserInfo.getUserId());
        videoUserInfo.setUserSubscribersCount(videoUploadUser.getSubscribersCount());

        logger.info("Mapped information VideoUserInfoDTO and response sent to frontend");
        return mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
    }

    private void updateViewCount(String videoId, MongoDatabase database) {
        logger.info("Video view count updating");
        MongoCollection<Document> collection = database.getCollection("videos");
        Bson filter = Filters.eq("_id", new ObjectId(videoId));
        Bson update = Updates.inc("viewCount", 1);
        UpdateResult updateResult = collection.updateOne(filter, update);

        if (updateResult.getModifiedCount() == 1)
            logger.info("Video view count updated !");
    }


    public VideoDTO likeVideo(String videoId, String userId) {
        return videoServiceLogic.likeVideo(videoId, userId);
    }

    public VideoDTO dislikeVideo(String videoId, String userId) {
        return videoServiceLogic.dislikeVideo(videoId, userId);
    }


    public List<VideoUserInfoDTO> getVideosAndUser(List<String> videos) {
        logger.info("Fetching Video and User information list of videos");
        return videos.stream().map(videoId -> {
            VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);

            return mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
        }).toList();
    }

    public List<VideoUserInfoDTO> getSuggestionVideos(int page, int size) {

        logger.info("Getting suggestion videos by random");

        // TO DO - Need to build real logic

        Page<Video> videoPage = videoRepo.findAllIds(PageRequest.of(page, size));

        if (videoPage.hasContent()) {
            ArrayList<Video> videoArrayList = new ArrayList<>(videoPage.getContent());
            Collections.shuffle(videoArrayList);
            List<String> videoIds = videoArrayList.stream().map(Video::getId).toList();
            return getVideosAndUser(videoIds);
        } else {
            return null;
        }
    }

    public List<WatchedVideoDTO> fetchWatchedVideos(String userId, int page, int size) {
        // Skipping elements PageNumber * PageSize
        logger.info("Fetching user watched videos and filter and structured by database query");
        int skip = page * size;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        List<WatchedVideo> watchedVideoList = watchedVideoRepo.getUserVideoWatchHistory(userId, skip, size);

        return mapperUtil.mapToList(watchedVideoList, WatchedVideoDTO.class);
    }

    public List<VideoUserInfoDTO> getShortVideo() {
        logger.info("Fetching short video from database");
        List<String> videosId = videoRepo.getShortsVideo();
        if (videosId != null) {
            return videosId.stream().map(this::getVideoUserInfo).toList();
        }
        return null;
    }

    public List<VideoUserInfoDTO> getSubscriptionVideos(int page, int size) {

        logger.info("Fetching videos from subscribed users");
        User user = userService.getCurrentUser();
        List<String> subscribedChannelIds = new ArrayList<>(user.getSubscribedToUsers());

        if (subscribedChannelIds.isEmpty())
            return null;

        PageRequest pageRequest = PageRequest.of(page, size);
        logger.info("Find latest video from user, database query fetched this result");
        List<String> videoIdList = videoRepo.findLatestVideoFromUsers(subscribedChannelIds, pageRequest);

        return getVideosAndUser(videoIdList);
    }

    public List<LikedVideoDTO> getLikedVideos(int page, int size) {

        User user = userService.getCurrentUser();

        List<LikeVideo> likeVideos = likeVideoRepo.getLikedVideos(user.getId(), page * size, size);

        return mapperUtil.mapToList(likeVideos, LikedVideoDTO.class);
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
        List<VideoUserInfo> videoUserInfoList = videoRepo.findVideosByTopics(topic);
        Optional<List<VideoUserInfo>> videoUserInfoOptional = Optional.ofNullable(videoUserInfoList);
        logger.info("List of videos found: {} and it mapped to Video User Info DTO for frontend", videoUserInfoList == null? 0:videoUserInfoList.size());
        return mapperUtil.mapToList(videoUserInfoOptional.orElse(new ArrayList<>()), VideoUserInfoDTO.class);
    }
}
