package com.karthick.videosharingapp.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document( collection = "likedVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class VideoLike extends VideoUserInfo{
    @Id
    private String id;

    private Instant likedAt;

    private Set<String> likeTopics;
}
