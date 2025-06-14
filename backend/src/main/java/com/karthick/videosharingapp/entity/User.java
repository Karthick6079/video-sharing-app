package com.karthick.videosharingapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    @JsonProperty(value = "nickname")
    private String name;

    @JsonProperty(value="name")
    private String displayName;

    @JsonProperty(value = "family_name")
    private String firstname;

    @JsonProperty(value = "given_name")
    private String lastname;

    private String picture;
    private String sub;
    private String email;
    private Instant createdAt;




}
