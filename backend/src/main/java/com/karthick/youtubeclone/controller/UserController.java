package com.karthick.youtubeclone.controller;


import com.karthick.youtubeclone.dto.UserDTO;
import com.karthick.youtubeclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.services.path}/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return userService.registerUser(jwt.getTokenValue());
    }
    @GetMapping("/profile-info")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getUserProfileInformation(){

       return userService.getUserProfileInformation();
    }

    @PutMapping("/subscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void subscribe(@RequestParam String userId){
        userService.subscribe(userId);
    }
    @PutMapping("/unsubscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unsubscribe(@RequestParam String userId){
        userService.unsubscribe(userId);
    }

}
