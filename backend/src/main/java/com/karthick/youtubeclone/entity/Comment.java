package com.karthick.youtubeclone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    private String id;
    private String text;
    private String videoId;
    private String userId;
    private String username;
    private String picture;
    private Long likes;
    private Long disLikes;
    private List<Comment> reply;
    private Long commentCreatedTime;
}
