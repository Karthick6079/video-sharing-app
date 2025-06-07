package com.karthick.videosharingapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReactionCountResponse {
    private long likes;
    private long dislikes;
}
