package com.karthick.videosharingapp.interfaces;

import java.util.List;

public interface VideoLikeRepositoryCustom {

    List<String> findRecentLikedTopicsByUsers(String userId, int days);
}
