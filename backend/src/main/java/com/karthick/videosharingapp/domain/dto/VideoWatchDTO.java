package com.karthick.videosharingapp.domain.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VideoWatchDTO extends VideoUserInfoDTO {

    private String id;

    private List<String> watchTopics;

    private Instant watchedAt;
}
