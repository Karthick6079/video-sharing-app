package com.karthick.videosharingapp.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document( collection = "dislikedVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class VideoDislike extends VideoUserInfo{
    @Id
    private String id;

    private Instant dislikedAt;

    private Set<String> dislikeTopics;

}
