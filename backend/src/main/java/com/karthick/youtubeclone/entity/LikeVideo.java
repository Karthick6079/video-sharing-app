package com.karthick.youtubeclone.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document( collection = "likeVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class LikeVideo extends  VideoUserInfo{
    @Id
    private String id;

    private LocalDateTime likedOn;

    private Set<String> likeTopics;
}
