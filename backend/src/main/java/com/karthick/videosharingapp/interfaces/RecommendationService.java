package com.karthick.videosharingapp.interfaces;

import com.karthick.videosharingapp.domain.dto.VideoUserInfoDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecommendationService {


    List<VideoUserInfoDTO> getRecommendationVideos(String userId, Pageable pageable);

    default List<VideoUserInfoDTO> getRecommendationVideos(Pageable pageable) {
        return null;
    }
}
