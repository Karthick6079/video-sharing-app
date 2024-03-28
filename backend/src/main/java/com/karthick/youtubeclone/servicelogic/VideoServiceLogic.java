package com.karthick.youtubeclone.servicelogic;

import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.service.LikedVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceLogic {

    private final LikedVideoService likedVideoService;


    // Case - 1
    // if user already liked video and now revoking the like means
    // Decrement like count and remove video from like list

    // Case -2
    // if user already disliked video and now the likes the vide
    // Decrement dislike count and remove video from disliked list
    // Increment like count and add video to liked list

    // Case - 3
    // Newly liking the video, increment like count and add to liked list
    public void likeVideo(Video video, User user,  String videoId){

        String userId = user.getId();

        if(user.isVideoLikedByUser(videoId)){
            video.decrementLikeCount();
            user.removeFromLikedVideo(videoId);
            likedVideoService.removeLikedVideoFromDB(userId, videoId);
        } else if(user.isVideoDisLikedByUser(videoId)){
            video.decrementDisLikeCount();
            user.removeFromDisLikedVideo(videoId);
            video.incrementLikeCount();
            user.addToLikedVideo(videoId);
            likedVideoService.addLikedVideoInDB(userId, videoId, video.getTags());
        } else{
            video.incrementLikeCount();
            user.addToLikedVideo(videoId);
            likedVideoService.addLikedVideoInDB(userId, videoId, video.getTags());
        }

    }

    public void dislikeVideo(Video video, User user,  String videoId){
        String userId = user.getId();
        if(user.isVideoDisLikedByUser(videoId)){
            video.decrementDisLikeCount();
            user.removeFromDisLikedVideo(videoId);
        } else if(user.isVideoLikedByUser(videoId)){
            video.decrementLikeCount();
            user.removeFromLikedVideo(videoId);
           // Removing the entry from DB
            likedVideoService.removeLikedVideoFromDB(userId, videoId);
            video.incrementDisLikeCount();
            user.addToDisLikedVideo(videoId);
        } else{
            video.incrementDisLikeCount();
            user.addToDisLikedVideo(videoId);
            likedVideoService.removeLikedVideoFromDB(userId, videoId);
        }

    }
}
