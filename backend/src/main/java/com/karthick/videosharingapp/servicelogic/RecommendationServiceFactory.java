package com.karthick.videosharingapp.servicelogic;

import com.karthick.videosharingapp.interfaces.RecommendationService;
import com.karthick.videosharingapp.servicelogic.recommendation.GuestRecommendationServiceServiceLogic;
import com.karthick.videosharingapp.servicelogic.recommendation.UserRecommendationServiceServiceLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendationServiceFactory {

    private final UserRecommendationServiceServiceLogic userRecommendationServiceServiceLogic;

    private final GuestRecommendationServiceServiceLogic guestRecommendationServiceServiceLogic;


    public RecommendationService getRecommendationService(boolean isUserLoggedIn){
        return isUserLoggedIn? userRecommendationServiceServiceLogic: guestRecommendationServiceServiceLogic;
    }
}
