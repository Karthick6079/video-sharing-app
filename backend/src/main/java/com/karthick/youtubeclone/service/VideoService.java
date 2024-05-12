package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.dto.*;
import com.karthick.youtubeclone.entity.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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


    public UploadVideoResponse uploadFile(MultipartFile file) {
        String url = s3Service.uploadFile(file);

        Video video = new Video();
        video.setVideoUrl(url);

        Video savedVideo =  videoRepo.save(video);

        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public UploadVideoResponse uploadThumbnail(MultipartFile file, String videoId) {
        String url = s3Service.uploadFile(file);

        Video savedVideo = findVideoById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + videoId));
        savedVideo.setThumbnailUrl(url);

        videoRepo.save(savedVideo);

        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public VideoDTO editVideoMetaData(VideoDTO videoDto) {
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

        videoRepo.save(savedVideo);

        return videoDto;

    }

    public Optional<Video> findVideoById(String id) {
        return videoRepo.findById(id);
    }


    private Video getVideoFromDB(String id){
        return findVideoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + id));
    }

    public VideoUserInfoDTO getVideoUserInfo(String videoId) {
        // 1. Get requested video and update it viewCount by 1
        MongoDatabase database = mongoClient.getDatabase("videoStreamingDB");
        updateViewCount(videoId, database);

        // 2. Get video and required user info from DB
        VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);
        User currentUser = userService.getCurrentUser();


        // 3. Update watch history table
        if(currentUser != null)
            watchedVideoService.updateWatchHistory(videoUserInfo, database, currentUser);

        return mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
    }

    private void updateViewCount(String videoId, MongoDatabase database) {
        MongoCollection<Document> collection = database.getCollection("videos");
        Bson filter = Filters.eq("_id", new ObjectId(videoId));
        Bson update  = Updates.inc("viewCount", 1);
        UpdateResult updateResult = collection.updateOne(filter, update);

        if( updateResult.getModifiedCount() == 1)
            System.out.println("Video view count updated !");
    }


    public VideoDTO likeVideo(String videoId, String userId){
        return videoServiceLogic.likeVideo(videoId, userId);
    }

    public VideoDTO dislikeVideo(String videoId, String userId){
        return videoServiceLogic.likeVideo(videoId, userId);
    }


    public List<VideoUserInfoDTO> getVideosAndUser(List<String> videos) {
        return videos.stream().map(videoId -> {
            VideoUserInfo videoUserInfo = videoRepo.getVideoUserInfo(videoId);

            return mapperUtil.map(videoUserInfo, VideoUserInfoDTO.class);
        }).toList();
    }

    public List<VideoUserInfoDTO> getSuggestionVideos(int page, int size){

         Page<Video> videoPage = videoRepo.findAllIds(PageRequest.of(page,size));

         if(videoPage.hasContent()){
             ArrayList<Video> videoArrayList = new ArrayList<>(videoPage.getContent());
             Collections.shuffle(videoArrayList);
             List<String> videoIds = videoArrayList.stream().map(Video::getId).toList();
             return getVideosAndUser(videoIds);
         } else{
             return null;
         }
    }

    public List<WatchedVideoDTO> fetchWatchedVideos(String userId, int page, int size) {
        // Skipping elements PageNumber * PageSize
        int skip = page * size;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        List<WatchedVideo> watchedVideoList   = watchedVideoRepo.getUserVideoWatchHistory(userId, skip, size);

        return mapperUtil.mapToList(watchedVideoList, WatchedVideoDTO.class);
    }

    public List<VideoUserInfoDTO> getShortVideo(){
        List<String> videosId = videoRepo.getShortsVideo();
        if(videosId != null){
            return videosId.stream().map(this::getVideoUserInfo).toList();
        }
        return null;
    }

    public List<VideoUserInfoDTO> getSubscriptionVideos(int page, int size) {

        User user = userService.getCurrentUser();
        List<String> subscribedChannelIds = new ArrayList<>(user.getSubscribedToUsers());

        if (subscribedChannelIds.size() == 0)
            return null;

        PageRequest pageRequest = PageRequest.of(page, size);
        List<String> videoIdList = videoRepo.findLatestVideoFromUsers(subscribedChannelIds, pageRequest);

        return getVideosAndUser(videoIdList);
    }

    public List<LikedVideoDTO> getLikedVideos(int page, int size) {

        User user = userService.getCurrentUser();
        List<String> likedVideosId = new ArrayList<>(user.getLikedVideos());

        if (likedVideosId.size() == 0)
            return null;

        List<LikeVideo> likeVideos = likeVideoRepo.getLikedVideos(user.getId(), page * size, size);

        return mapperUtil.mapToList(likeVideos, LikedVideoDTO.class);
    }

    public List<VideoUserInfoDTO> getSearchedVideos(String searchText){
           List<VideoUserInfo> videoUserInfoList = videoRepo.findVideosBySearchText(searchText);
           Optional<List<VideoUserInfo>> videoUserInfoOptional = Optional.ofNullable(videoUserInfoList);
        return mapperUtil.mapToList(videoUserInfoOptional.orElse(new ArrayList<>()), VideoUserInfoDTO.class);
    }

    public List<String> getTrendingTopics(){
        List<String> topics = videoRepo.getTrendingTopics();
        return removeDuplicateTopics(topics);

    }

    private List<String> removeDuplicateTopics(List<String> topics){

        List<String> distinctTopics =  new ArrayList<>();

        if(topics != null){
            distinctTopics = Arrays.stream(topics.toArray(new String[0])).distinct().toList();
            return distinctTopics;
        }

        return distinctTopics;
    }

    public List<VideoUserInfoDTO> getVideosByTopic(String topic){
        List<VideoUserInfo> videoUserInfoList = videoRepo.findVideosByTopics(topic);
        Optional<List<VideoUserInfo>> videoUserInfoOptional = Optional.ofNullable(videoUserInfoList);
        return mapperUtil.mapToList(videoUserInfoOptional.orElse(new ArrayList<>()), VideoUserInfoDTO.class);
    }
}
