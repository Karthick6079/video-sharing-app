package com.karthick.youtubeclone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
    private AtomicLong likes = new AtomicLong(0);
    private AtomicLong disLikes = new AtomicLong(0);
    private Set<String> tags;
    private VideoStatus videoStatus;
    private String videoUrl;
    private String thumbnailUrl;
    private List<Comment> commentList = new CopyOnWriteArrayList<>();
    private AtomicLong viewCount = new AtomicLong(0);

    public void increaseViewCount(){
        this.getViewCount().incrementAndGet();
    }

    public void incrementLikeCount(){
        this.getLikes().incrementAndGet();
    }

    public void decrementLikeCount(){
        this.getLikes().decrementAndGet();
    }

    public void incrementDisLikeCount(){
        this.getDisLikes().incrementAndGet();
    }

    public void decrementDisLikeCount(){
        this.getDisLikes().decrementAndGet();
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

}
