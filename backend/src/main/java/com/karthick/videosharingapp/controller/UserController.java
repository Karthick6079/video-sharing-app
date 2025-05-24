package com.karthick.videosharingapp.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.karthick.videosharingapp.domain.dto.ChannelInfoDTO;
import com.karthick.videosharingapp.domain.dto.LikedVideoDTO;
import com.karthick.videosharingapp.domain.dto.UserDTO;
import com.karthick.videosharingapp.domain.dto.WatchedVideoDTO;
import com.karthick.videosharingapp.service.CommonService;
import com.karthick.videosharingapp.service.UserService;
import com.karthick.videosharingapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.services.path}/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    private final CommonService commonService;

    private final MapperUtil mapperUtil;



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
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelInfoDTO subscribe(@RequestParam String userId){
        return userService.subscribe(userId);
    }

    @PutMapping("/unsubscribe")
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChannelInfoDTO unsubscribe(@RequestParam String userId){
        ;
        return userService.unsubscribe(userId);
    }

    @GetMapping("/videos-history")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WatchedVideoDTO> videosHistory(@RequestParam( value = "page", defaultValue = "0" ) int page,
                                               @RequestParam( value = "size", defaultValue = "6") int size) throws JsonProcessingException {
        return commonService.getWatchedVideos(page, size);
    }

    @GetMapping("/liked-videos")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<LikedVideoDTO> getLikedVideos(@RequestParam( value = "page", defaultValue = "0" ) int page,
                                              @RequestParam( value = "size", defaultValue = "6") int size){
        return commonService.getLikedVideos(page, size);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserById(@PathVariable String id){
        return mapperUtil.map(userService.getUserById(id), UserDTO.class);
    }



}
