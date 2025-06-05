package com.karthick.videosharingapp.domain.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChannelInfoDTO {

    private String name;

    private String displayName;

    private Long subscribersCount;

    private boolean isUserSubscribed = false;

}
