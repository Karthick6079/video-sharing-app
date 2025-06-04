package com.karthick.videosharingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
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
    private AtomicLong dislikes = new AtomicLong(0);
    private List<Comment> reply;
    private Instant createdAt;

    public void incrementLike(){
        this.getLikes().incrementAndGet();
    }

    public void decrementLike(){
        this.getLikes().decrementAndGet();
    }

    public void incrementDislike(){
        this.getDislikes().incrementAndGet();
    }

    public void decrementDislike(){
        this.getDislikes().decrementAndGet();
    }
}
