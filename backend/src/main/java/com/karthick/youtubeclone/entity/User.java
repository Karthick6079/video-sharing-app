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

@Document("User")
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
    @JsonIgnore
    private Set<String> subscribers = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> videoHistory = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> likedVideos = ConcurrentHashMap.newKeySet();
    @JsonIgnore
    private Set<String> dislikedVideos = ConcurrentHashMap.newKeySet();

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


}
