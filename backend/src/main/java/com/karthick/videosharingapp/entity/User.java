package com.karthick.videosharingapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private Instant createdAt;

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



}
