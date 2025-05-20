package com.karthick.videosharingapp.entity;

import lombok.Data;

import java.time.Instant;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class VideoUserInfo {

    private String id;
    private String videoId;
    private String userId;
    private String title;
    private String description;
    private AtomicLong likes;
    private AtomicLong dislikes;
    private AtomicLong viewCount;
    private List<String> tags;
    private String videoStatus;
    private String videoUrl;
    private String thumbnailUrl;
    private Instant createdAt;
    private Instant publishedAt;
    private String username; //uniquename
    private String userDisplayName;
    private String userPicture;
    private AtomicLong channelSubscribersCount;
    private boolean isCurrentUserSubscribedToChannel;

}
