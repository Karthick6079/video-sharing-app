package com.karthick.videosharingapp.servicelogic;

import com.karthick.videosharingapp.domain.dto.VideoDTO;
import com.karthick.videosharingapp.entity.*;
import com.karthick.videosharingapp.repository.VideoDislikeRepository;
import com.karthick.videosharingapp.repository.VideoLikeRepository;
import com.karthick.videosharingapp.repository.VideoRepository;
import com.karthick.videosharingapp.service.VideoLikeService;
import com.karthick.videosharingapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VideoServiceLogic {

    private final VideoLikeService videoLikeService;

    private final VideoRepository videoRepo;

    private final VideoLikeRepository videoLikeRepository;

    private final VideoDislikeRepository videoDislikeRepository;

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

        Optional<VideoLike> likeVideoOp = videoLikeRepository.findByUserIdAndVideoId(userId, videoId);

        Optional<VideoDislike> dislikeVideoOp = videoDislikeRepository.findByUserIdAndVideoId(userId, videoId);

        if (likeVideoOp.isPresent()) {

            videoOp.ifPresent(
                    video -> {
                        logger.info("Likes count decrementing when video already liked");
                        video.decrementLikeCount();
                        logger.info("Saving the video in db and deleting liked video info in db ");
                        videoRepo.save(video);
                        videoLikeRepository.deleteById(likeVideoOp.get().getId());
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
                        videoDislikeRepository.deleteById(dislikeVideoOp.get().getId());
                    }
            );
        } else {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Increasing video likes count");
                        video.incrementLikeCount();
                        VideoLike videoLike = createLikeVideo(videoId, userId, video.getTags());
                        logger.info("Saving the video in db and liked video info in db ");
                        videoRepo.save(video);
                        videoLikeRepository.save(videoLike);
                    }
            );
        }

        logger.info("Like count after increment is: {} ", videoOp.get().getLikes());

        return mapperUtil.map(videoOp.orElse(new Video()), VideoDTO.class);
    }


    public VideoDTO dislikeVideo(String videoId, String userId) {

        logger.info("Fetching video information from database");

        Optional<Video> videoOp = videoRepo.findById(videoId);

        logger.info("dislike count before increment is: {} ", videoOp.get().getDislikes());

        Optional<VideoLike> likeVideoOp = videoLikeRepository.findByUserIdAndVideoId(userId, videoId);

        Optional<VideoDislike> dislikeVideoOp = videoDislikeRepository.findByUserIdAndVideoId(userId, videoId);

        if (dislikeVideoOp.isPresent()) {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Likes count decreasing when video already disliked");
                        video.decrementDisLikeCount();
                        logger.info("Saving the video in db and deleting disliked video info in db");
                        videoRepo.save(video);
                        videoDislikeRepository.deleteById(dislikeVideoOp.get().getId());
                    }
            );
        } else if (likeVideoOp.isPresent()) { //If user already disliked comments means decrement the dislike count and inc like count
            videoOp.ifPresent(
                    video -> {
                        logger.info("dislikes count increasing and decrease like count when video already liked");
                        video.incrementDisLikeCount();
                        video.decrementLikeCount();
                        videoRepo.save(video);
                        videoLikeRepository.deleteById(likeVideoOp.get().getId());
                    }
            );
        } else {
            videoOp.ifPresent(
                    video -> {
                        logger.info("Increasing video dislikes count");
                        video.incrementDisLikeCount();
                        VideoDislike videoDislike = createDislikeVideo(videoId, userId, video.getTags());
                        logger.info("Saving the video in db and disliked video info in db ");
                        videoRepo.save(video);
                        videoDislikeRepository.save(videoDislike);
                    }
            );
        }

        logger.info("dislike count after increment is: {} ", videoOp.get().getDislikes());

        return mapperUtil.map(videoOp.orElse(new Video()), VideoDTO.class);
    }

    public VideoLike createLikeVideo(String videoId, String userId, Set<String> likeTopics) {

        VideoLike videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setLikeTopics(likeTopics);
        videoLike.setLikedAt(Instant.now());

        return videoLike;
    }

    public VideoDislike createDislikeVideo(String videoId, String userId, Set<String> likeTopics) {

        VideoDislike videoDislike = new VideoDislike();
        videoDislike.setVideoId(videoId);
        videoDislike.setUserId(userId);
        videoDislike.setDislikeTopics(likeTopics);
        videoDislike.setDislikedAt(Instant.now());

        return videoDislike;
    }

}
