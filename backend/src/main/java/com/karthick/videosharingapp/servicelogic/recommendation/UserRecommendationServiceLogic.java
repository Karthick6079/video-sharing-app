package com.karthick.videosharingapp.servicelogic.recommendation;

import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.repository.LikeVideoRepo;
import com.karthick.videosharingapp.repository.SubscriptionRepo;
import com.karthick.videosharingapp.repository.VideoRepository;
import com.karthick.videosharingapp.repository.WatchRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class UserRecommendationServiceLogic {
    
    private final VideoRepository videoRepository;
    
    private final WatchRepository watchRepository;
    
    private final LikeVideoRepo likeVideoRepo;

    private final SubscriptionRepo subscriptionRepo;
    
    
    public Page<Video> getUserRecommendation(String userId, Pageable pageable){
        
        // 1. Find user interested topics
        
        List<String> watchedTopics = watchRepository.findWatchTopicsByUserId(userId);
        List<String> likedTopics = likeVideoRepo.findLikeTopicsByUserId(userId);

        // The channels user subscribed
        List<String> channelIds = subscriptionRepo.findChannelIdBySubscriberId(userId);

        Set<String> userInterestedTopicSet = new HashSet<>();
        userInterestedTopicSet.addAll(watchedTopics);
        userInterestedTopicSet.addAll(likedTopics);


        List<String> userInterestedTopicList = new ArrayList<>(userInterestedTopicSet);

        
        // 2. Find videos based on user interest
        List<Video> contentBasedVideos = videoRepository.findVideosByTags(userInterestedTopicList);

        List<Video> subscribedUsersVideos = videoRepository.findAllByUserId(channelIds);
        
        
        
       return null; 
    }
    
    
}
