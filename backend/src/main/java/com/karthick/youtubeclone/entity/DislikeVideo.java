package com.karthick.youtubeclone.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document( collection = "dislikeVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class DislikeVideo extends  VideoUserInfo{
    @Id
    private String id;

    private LocalDateTime dislikedOn;

    private Set<String> dislikeTopics;
}
