package com.karthick.videosharingapp.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WatchedVideoDTO extends VideoUserInfoDTO {

    private String id;

    private List<String> watchTopics;

    private LocalDateTime watchedOn;
}
