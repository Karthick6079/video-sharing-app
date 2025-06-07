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
    private String name;
    private String picture;
    private String sub;
    private String email;
    @JsonProperty(value = "given_name")
    private String lastname;
    @JsonProperty(value = "family_name")
    private String firstname;
    @JsonProperty(value = "nickname")
    private String nickname;
    private Instant createdAt;




}
