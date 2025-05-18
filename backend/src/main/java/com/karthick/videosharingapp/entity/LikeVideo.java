package com.karthick.videosharingapp.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

@Document( collection = "likeVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class LikeVideo{
    @Id
    private String id;

    private String userId;

    private String videoId;

    private LocalDateTime likedOn;

    private Set<String> likeTopics;
}
