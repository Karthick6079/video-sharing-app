package com.karthick.videosharingapp.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;


@Document(collection = "watchedVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class Watch {

    @Id
    private String id;

    private String userId;

    private String videoId;

    private LocalDateTime watchedOn = LocalDateTime.now();

    private Set<String> watchTopics;

}
