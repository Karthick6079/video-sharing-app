package com.karthick.youtubeclone.dto;

import com.karthick.youtubeclone.entity.Video;
import lombok.Data;

import java.util.List;


@Data
public class LatestVideoDTO {

    private String userId;
    private List<Video> videos;
}
