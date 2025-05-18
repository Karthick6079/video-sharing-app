package com.karthick.videosharingapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class LikedVideoDTO extends  VideoUserInfoDTO{

    private String id;

    private LocalDateTime likedOn;

    private Set<String> likeTopics;
}
