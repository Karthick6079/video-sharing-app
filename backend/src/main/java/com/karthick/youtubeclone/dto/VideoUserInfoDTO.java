package com.karthick.youtubeclone.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class VideoUserInfoDTO {

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
    private LocalDateTime publishedDateAndTime;
    private String username; //uniquename
    private String userDisplayName;
    private String userPicture;
    private AtomicLong userSubscribersCount;

}
