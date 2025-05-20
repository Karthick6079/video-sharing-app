package com.karthick.videosharingapp.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Document( collection = "dislikeVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class DislikeVideo{
    @Id
    private String id;

    private String userId;

    private String videoId;

    private Instant dislikedAt;

    private Set<String> dislikeTopics;

}
