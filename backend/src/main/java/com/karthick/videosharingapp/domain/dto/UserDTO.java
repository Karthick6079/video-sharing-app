package com.karthick.videosharingapp.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Set;

;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String picture;
    private String sub;
    private String email;
    private String nickname;
    private Set<String> subscribedToUsers;
    private Set<String> subscribers;
    private Long subscribedToCount;
    private Long subscribersCount;
    private Instant createdAt;
}
