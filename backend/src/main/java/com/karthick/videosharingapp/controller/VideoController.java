package com.karthick.videosharingapp.controller;

import com.karthick.videosharingapp.domain.CompleteMultipartRequest;
import com.karthick.videosharingapp.domain.dto.ReactionCountResponse;
import com.karthick.videosharingapp.domain.dto.UploadVideoResponse;
import com.karthick.videosharingapp.domain.dto.VideoUserInfoDTO;
import com.karthick.videosharingapp.domain.dto.VideoDTO;
import com.karthick.videosharingapp.interfaces.MultiPartUploadService;
import com.karthick.videosharingapp.service.EmailSendService;
import com.karthick.videosharingapp.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.services.path}/video")
@RequiredArgsConstructor
@CrossOrigin
public class VideoController {

    private final VideoService videoService;

    private final EmailSendService emailSendService;

    private final MultiPartUploadService multiPartUploadService;

    private final Logger logger = LoggerFactory.getLogger(VideoController.class);


    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse upload(@RequestParam("file") MultipartFile file) {
        logger.info("The video controller received an request to upload video file to AWS S3");
        return videoService.uploadFile(file);
    }

    @PostMapping(path = "/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam String videoId) {
        logger.info("Upload video thumbnail to AWS S3 request received in video controller");
        return videoService.uploadThumbnail(file, videoId);
    }

    @PutMapping("/editMetadata")
    @ResponseStatus(HttpStatus.OK)
    public VideoDTO editVideoMetaData(@RequestBody VideoDTO videoDto) {
        logger.info("Update video metadata in database request received in video controller");
        logger.debug("The received video meta data: {}", videoDto);
        return videoService.editVideoMetaData(videoDto);
    }

    @GetMapping("/watch/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public VideoUserInfoDTO getVideoForWatch(@PathVariable String videoId) {
        logger.info("Get video information from database request received in video controller");
        return videoService.updateVideoWatchAndGetVideoDetails(videoId);
    }

    @PutMapping("/watch/{videoId}/like/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionCountResponse likeVideo(@PathVariable String videoId, @PathVariable String userId) {
        logger.info("Update like count of video request received in  video controller");
        return videoService.likeVideo(videoId, userId);
    }


    @PutMapping("/watch/{videoId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionCountResponse dislikeVideo(@PathVariable String videoId, @PathVariable String userId) {
        logger.info("Update dislike count of video request received in video controller");
        return videoService.dislikeVideo(videoId, userId);
    }

    @GetMapping("/suggestion-videos")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoUserInfoDTO> getSuggestedVideos(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "6") int size) {
        logger.info("Get new suggestion videos request received in video controller");
        logger.debug("The suggestion videos for page: {} and size: {}", page, size);
        return videoService.getRecommendationVideos(page, size);
    }

    @GetMapping("/short-video")
    public List<VideoUserInfoDTO> getShortsVideo(@RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "2") int size) {
        logger.info("Get shorts videos request received on video controller");
        return videoService.getShortVideo(page, size);
    }

    @PostMapping("/subscription-videos")
    public List<VideoUserInfoDTO> getSubscriptionVideos(@RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "6") int size) {
        logger.info("Get new subscription videos request received in video controller");
        logger.debug("The subscription videos for page: {} and size: {}", page, size);
        return videoService.getSubscriptionVideos(page, size);
    }

    @GetMapping("/search")
    public List<VideoUserInfoDTO> getSearchVideos(@RequestParam String searchText) {
        logger.info("Search the video based user given input text request received on video controller");
        return videoService.getSearchedVideos(searchText);
    }

    @GetMapping("/trending-topics")
    public List<String> getTrendingTopics() {
        logger.info("Get Trending topics for user request received on video controller");
        return videoService.getTrendingTopics();
    }

    @GetMapping("/topic-videos")
    public List<VideoUserInfoDTO> getVideosByTopic(@RequestParam String topic) {
        logger.info("Fetching videos by topic request received on video controller");
        return videoService.getVideosByTopic(topic);
    }

    @PutMapping("/update-video-watch/{videoId}")
    public void updateVideoWatch(@PathVariable String videoId){
        videoService.updateVideoWatchAndGetVideoDetails(videoId);
        logger.info("updated video watch in data models and get video user information");
    }


    /* Multi part video upload request mapping follows */

    @PostMapping("/upload/multipart/initiate")
    public ResponseEntity<Map<String, Object>> initiateMultiPart(@RequestParam String filename, @RequestParam String fileExtension){
        return ResponseEntity.ok(multiPartUploadService.initiateUpload(filename, fileExtension));
    }

    @GetMapping("/upload/multipart/url")
    public ResponseEntity<Map<String, String>> generatePresignerURL(@RequestParam String key, @RequestParam String uploadId,
                                                                    @RequestParam int partNumber){
        String presignedUrl = multiPartUploadService.generatePreSignedUrl(key, uploadId, partNumber);
        return ResponseEntity.ok(Map.of("url", presignedUrl));
    }

    @PostMapping("/upload/multipart/complete")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse completeMultipartUpload(@RequestBody CompleteMultipartRequest request){
        return multiPartUploadService.completeUpload(request);
    }

//    @PostMapping("/send-welcome-email")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void sendWelcomeEmail(@RequestParam String toEmail, @RequestParam String username){
//        emailSendService.sendWelcomeEmail(toEmail, username);
//    }




}
