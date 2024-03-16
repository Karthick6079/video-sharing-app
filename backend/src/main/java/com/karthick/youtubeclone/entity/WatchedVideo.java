package com.karthick.youtubeclone.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;



@Document(collection = "watchedVideos")
@Getter
@Setter
@RequiredArgsConstructor
public class WatchedVideo extends VideoUserInfo {

    @Id
    private String id;

    private LocalDateTime watchedOn;

}
