package com.karthick.youtubeclone.dto;

import com.karthick.youtubeclone.entity.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoDTO {

    private String id;
    private String title;
    private String description;
    private Set<String> tags;
    private VideoStatus videoStatus;
    private String videoUrl;
    private String thumbnailUrl;
    private Long likes;
    private Long dislikes;
    private Long viewCount;
    private UserDTO userDTO;


}
