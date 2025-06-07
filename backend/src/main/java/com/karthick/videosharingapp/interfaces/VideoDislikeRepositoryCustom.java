package com.karthick.videosharingapp.interfaces;

import java.util.List;

public interface VideoDislikeRepositoryCustom {

    List<String> findRecentDislikedTopicsByUsers(String userId, int days);
}
