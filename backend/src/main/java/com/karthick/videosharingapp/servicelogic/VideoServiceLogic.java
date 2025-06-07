package com.karthick.videosharingapp.servicelogic;

import com.karthick.videosharingapp.domain.dto.ReactionCountResponse;
import com.karthick.videosharingapp.domain.dto.VideoDTO;
import com.karthick.videosharingapp.entity.*;
import com.karthick.videosharingapp.repository.*;
import com.karthick.videosharingapp.service.VideoLikeService;
import com.karthick.videosharingapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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

    private final VideoLikeRepositoryCustomImpl videoLikeRepositoryCustom;

    private final VideoDislikeRepositoryCustomImpl videoDislikeRepositoryCustom;

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

    public ReactionCountResponse likeVideo(String videoId, String userId) {

        Optional<Video> videoOp = videoRepo.findById(videoId);

        if(videoOp.isPresent()){
            List<String> likeTopics = videoOp.get().getTags().stream().toList();
            ReactionCountResponse response = videoLikeRepositoryCustom.toggleLike(userId, videoId, likeTopics);
            logger.info("Like count updated");
            return response;
        }
        return null;
    }

    public ReactionCountResponse dislikeVideo(String videoId, String userId) {

        logger.info("Fetching video information from database");
        Optional<Video> videoOp = videoRepo.findById(videoId);


        if(videoOp.isPresent()){
            List<String> dislikeTopics = videoOp.get().getTags().stream().toList();
            ReactionCountResponse response = videoDislikeRepositoryCustom.toggleDislike(userId, videoId, dislikeTopics);
            logger.info("Dislike count updated");
            return response;
        }
        return null;
    }

}
