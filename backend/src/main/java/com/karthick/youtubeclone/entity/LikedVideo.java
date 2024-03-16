package com.karthick.youtubeclone.entity;


import jdk.jfr.Registered;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Document( collection = "likedVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class LikedVideo extends  VideoUserInfo{
    @Id
    private String id;

    private LocalDateTime likedOn;

    private Set<String> likedTopics;
}
