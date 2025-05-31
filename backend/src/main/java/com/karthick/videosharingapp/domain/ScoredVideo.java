package com.karthick.videosharingapp.domain;

import com.karthick.videosharingapp.entity.Video;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoredVideo {

    private Video video;
    private double score;
}
