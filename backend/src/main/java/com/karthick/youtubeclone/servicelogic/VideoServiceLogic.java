package com.karthick.youtubeclone.servicelogic;

import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.entity.*;
import com.karthick.youtubeclone.repository.DislikeVideoRepo;
import com.karthick.youtubeclone.repository.LikeVideoRepo;
import com.karthick.youtubeclone.repository.VideoRepository;
import com.karthick.youtubeclone.service.LikedVideoService;
import com.karthick.youtubeclone.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VideoServiceLogic {

    private final LikedVideoService likedVideoService;

    private final VideoRepository videoRepo;

    private final LikeVideoRepo likeVideoRepo;

    private final DislikeVideoRepo dislikeVideoRepo;

    private final MapperUtil mapperUtil;

    private final Logger logger = LoggerFactory.getLogger(VideoServiceLogic.class);


    // Case - 1
    // if user already liked video and now revoking the like means
    // Decrement like count and remove video from like list

    // Case -2
    // if user already disliked video and now the likes the vide
    // Decrement dislike count and remove video from disliked list
    // Increment like count and add video to liked list

    // Case - 3
    // Newly liking the video, increment like count and add to liked list

    public VideoDTO likeVideo(String videoId, String userId) {

        logger.info("Fetching video information from database");

        Optional<Video> videoOp = videoRepo.findById(videoId);

        logger.info("Like count before increment is: {} ", videoOp.get().getLikes());

        Optional<LikeVideo> likeVideoOp = likeVideoRepo.findByUserIdAndVideoId(userId, videoId);

        Optional<DislikeVideo> dislikeVideoOp = dislikeVideoRepo.findByUserIdAndVideoId(userId, videoId);

        if (likeVideoOp.isPresent()) {

            videoOp.ifPresent(
                    video -> {
                        logger.info("Likes count decrementing when video already liked");
                        video.decrementLikeCount();
                        logger.info("Saving the video in db and deleting liked video info in db ");
                        videoRepo.save(video);
                        likeVideoRepo.deleteById(likeVideoOp.get().getId());
                    }
            );
        } else if (dislikeVideoOp.isPresent()) { //If user already disliked comments means decrement the dislike count and inc like count
            videoOp.ifPresent(
                    video -> {
                        logger.info("Likes count increasing and decrease dislike count when video already disliked");
                        video.incrementLikeCount();
                        video.decrementDisLikeCount();
                        logger.info("Saving the video in db and deleting disliked video info in db ");
                        videoRepo.save(video);
                        dislikeVideoRepo.deleteById(dislikeVideoOp.get().getId());
                    }
            );
        } else {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Increasing video likes count");
                        video.incrementLikeCount();
                        LikeVideo likeVideo = createLikeVideo(videoId, userId, video.getTags());
                        logger.info("Saving the video in db and liked video info in db ");
                        videoRepo.save(video);
                        likeVideoRepo.save(likeVideo);
                    }
            );
        }

        logger.info("Like count after increment is: {} ", videoOp.get().getLikes());

        return mapperUtil.map(videoOp.orElse(new Video()), VideoDTO.class);
    }


    public VideoDTO dislikeVideo(String videoId, String userId) {

        logger.info("Fetching video information from database");

        Optional<Video> videoOp = videoRepo.findById(videoId);

        logger.info("dislike count before increment is: {} ", videoOp.get().getDisLikes());

        Optional<LikeVideo> likeVideoOp = likeVideoRepo.findByUserIdAndVideoId(userId, videoId);

        Optional<DislikeVideo> dislikeVideoOp = dislikeVideoRepo.findByUserIdAndVideoId(userId, videoId);

        if (dislikeVideoOp.isPresent()) {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Likes count decreasing when video already disliked");
                        video.decrementDisLikeCount();
                        logger.info("Saving the video in db and deleting disliked video info in db");
                        videoRepo.save(video);
                        dislikeVideoRepo.deleteById(dislikeVideoOp.get().getId());
                    }
            );
        } else if (likeVideoOp.isPresent()) { //If user already disliked comments means decrement the dislike count and inc like count
            videoOp.ifPresent(
                    video -> {
                        logger.info("dislikes count increasing and decrease like count when video already liked");
                        video.incrementDisLikeCount();
                        video.decrementLikeCount();
                        videoRepo.save(video);
                        likeVideoRepo.deleteById(likeVideoOp.get().getId());
                    }
            );
        } else {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Increasing video dislikes count");
                        video.incrementDisLikeCount();
                        DislikeVideo dislikeVideo = createDislikeVideo(videoId, userId, video.getTags());
                        logger.info("Saving the video in db and disliked video info in db ");
                        videoRepo.save(video);
                        dislikeVideoRepo.save(dislikeVideo);
                    }
            );
        }

        logger.info("dislike count after increment is: {} ", videoOp.get().getDisLikes());

        return mapperUtil.map(videoOp.orElse(new Video()), VideoDTO.class);
    }

    public LikeVideo createLikeVideo(String videoId, String userId, Set<String> likeTopics) {

        LikeVideo likeVideo = new LikeVideo();
        likeVideo.setVideoId(videoId);
        likeVideo.setUserId(userId);
        likeVideo.setLikeTopics(likeTopics);
        likeVideo.setLikedOn(LocalDateTime.now());

        return likeVideo;
    }

    public DislikeVideo createDislikeVideo(String videoId, String userId, Set<String> likeTopics) {

        DislikeVideo dislikeVideo = new DislikeVideo();
        dislikeVideo.setVideoId(videoId);
        dislikeVideo.setUserId(userId);
        dislikeVideo.setDislikeTopics(likeTopics);
        dislikeVideo.setDislikedOn(LocalDateTime.now());

        return dislikeVideo;
    }

}
