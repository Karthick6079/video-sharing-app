package com.karthick.youtubeclone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document("Video")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    private String id;
    private String title;
    private String description;
    private String userId;
    private Long likes;
    private Long disLikes;
    private Set<String> tags;
    private VideoStatus videoStatus;
    private String videoUrl;
    private String thumbnailUrl;
    private List<Comment> comments;
}
