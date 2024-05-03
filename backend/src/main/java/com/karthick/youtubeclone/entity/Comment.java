package com.karthick.youtubeclone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    private AtomicLong likes =  new AtomicLong(0);
    private AtomicLong disLikes = new AtomicLong(0);
    private List<Comment> reply;
    private Long commentCreatedTime;

    public void incrementLike(){
        this.getLikes().incrementAndGet();
    }

    public void decrementLike(){
        this.getLikes().decrementAndGet();
    }

    public void incrementDislike(){
        this.getDisLikes().incrementAndGet();
    }

    public void decrementDislike(){
        this.getDisLikes().decrementAndGet();
    }
}
