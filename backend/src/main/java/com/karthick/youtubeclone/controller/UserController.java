package com.karthick.youtubeclone.controller;


import com.karthick.youtubeclone.dto.UserDTO;
import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.service.CommonService;
import com.karthick.youtubeclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${spring.services.path}/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    private final CommonService commonService;



    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return userService.registerUser(jwt);
    }
    @GetMapping("/profile-info")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserProfileInformation(){

       return userService.getUserProfileInformation();
    }

    @PutMapping("/subscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean subscribe(@RequestParam String userId){
        userService.subscribe(userId);
        return true;
    }
    @PutMapping("/unsubscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean  unsubscribe(@RequestParam String userId){
        userService.unsubscribe(userId);
        return true;
    }

    @GetMapping("/videos-history")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<VideoDTO> videosHistory(){
        return commonService.getWatchedVideos();
    }


}
