package com.karthick.videosharingapp.interfaces;

import com.karthick.videosharingapp.entity.Video;

import java.util.List;

public interface VideoRepositoryCustom {

     List<Video> findTrendingVideos(int limit);
}
