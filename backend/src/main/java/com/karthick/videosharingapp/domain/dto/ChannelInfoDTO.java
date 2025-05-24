package com.karthick.videosharingapp.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@RequiredArgsConstructor
public class ChannelInfoDTO {

    private String name;

    private String displayName;

    private Long subscriberCount;

    private boolean isUserSubscribed = false;

}
