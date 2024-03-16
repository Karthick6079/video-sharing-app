package com.karthick.youtubeclone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Document("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @JsonIgnore
    private String firstName;
    @JsonIgnore
    private String lastName;
    private String name;
    private String picture;
    private String sub;
    private String email;
    @JsonProperty(value = "given_name")
    private String givenName;
    @JsonProperty(value = "family_name")
    private String familyName;
    @JsonProperty(value = "nickname")
    private String nickname;
    @JsonIgnore
    private Set<String> subscribedToUsers = ConcurrentHashMap.newKeySet();
    private AtomicLong subscribedToCount = new AtomicLong(0);
    @JsonIgnore
    private Set<String> subscribers = ConcurrentHashMap.newKeySet();
    private AtomicLong subscribersCount = new AtomicLong(0);
    @JsonIgnore
    private List<String> videoHistory = new CopyOnWriteArrayList<>();
    @JsonIgnore
    private Set<String> likedVideos = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> dislikedVideos = ConcurrentHashMap.newKeySet();

    public void incrementSubscriberCount(){
        this.subscribersCount.incrementAndGet();
    }

    public void decrementSubscriberCount(){
        this.subscribersCount.decrementAndGet();
    }

    public void incrementSubscribedToCount(){
        this.subscribedToCount.incrementAndGet();
    }

    public void decrementSubscribedToCount(){
        this.subscribedToCount.decrementAndGet();
    }

    public void addToLikedVideo(String videoId){
        this.likedVideos.add(videoId);
    }

    public void removeFromLikedVideo(String videoId){
        this.likedVideos.remove(videoId);
    }

    public void addToDisLikedVideo(String videoId){
        this.dislikedVideos.add(videoId);
    }

    public void removeFromDisLikedVideo(String videoId){
        this.dislikedVideos.remove(videoId);
    }

    public boolean isVideoLikedByUser(String id){
        return this.getLikedVideos().stream().anyMatch( videoId -> videoId.equals(id));
    }
    public boolean isVideoDisLikedByUser(String id){
        return this.getDislikedVideos().stream().anyMatch( videoId -> videoId.equals(id));
    }

    public void addToVideoHistory(String videoId){
        this.videoHistory.add(videoId);
    }


}
