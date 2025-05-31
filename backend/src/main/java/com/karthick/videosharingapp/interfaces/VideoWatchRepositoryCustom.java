package com.karthick.videosharingapp.interfaces;

import java.util.List;

public interface VideoWatchRepositoryCustom {

    List<String> findRecentWatchTopicsByUsers(String userId, int days);
}
