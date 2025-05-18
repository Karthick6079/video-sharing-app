package com.karthick.videosharingapp.dto;

import com.karthick.videosharingapp.entity.Video;
import lombok.Data;

import java.util.List;


@Data
public class LatestVideoDTO {

    private String userId;
    private List<Video> videos;
}
