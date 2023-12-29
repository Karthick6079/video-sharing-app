package com.karthick.youtubeclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String name;
    private String picture;
    private String sub;
    private String email;
    private String nickname;
    private Set<String> subscribedToUsers;
    private Set<String> subscribers;
    private Set<String> videoHistory;
    private Set<String> likedVideos;
    private Set<String> dislikedVideos;
}
