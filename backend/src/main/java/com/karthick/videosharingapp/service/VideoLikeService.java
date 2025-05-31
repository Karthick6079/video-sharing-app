package com.karthick.videosharingapp.service;

import com.karthick.videosharingapp.entity.VideoLike;
import com.karthick.videosharingapp.repository.VideoLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VideoLikeService {


    private final VideoLikeRepository videoLikeRepository;



    /**
     * This method update watched video into table if the specific video is already watched in current day
     * the existing video document data updated, Otherwise new document will be inserted at collection
     *
     * @param videoUserInfo
     * @param database
     */
    public void addLikedVideoInDB(String userId, String videoId, Set<String> topics) {


        VideoLike videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setLikedAt(Instant.now());
        videoLike.setLikeTopics(topics);

        videoLikeRepository.save(videoLike);


    }


    public void removeLikedVideoFromDB(String userId, String videoId){

        videoLikeRepository.deleteByUserIdAndVideoId(userId, videoId);

    }


}
