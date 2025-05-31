package com.karthick.videosharingapp.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class VideoLikeDTO extends  VideoUserInfoDTO{

    private String id;

    private Instant likedAt;

    private Set<String> likeTopics;
}
