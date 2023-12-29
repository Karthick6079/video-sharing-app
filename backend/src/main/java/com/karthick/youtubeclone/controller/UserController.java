package com.karthick.youtubeclone.controller;


import com.karthick.youtubeclone.dto.UserDto;
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
    public String register(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        userService.registerUser(jwt.getTokenValue());

        return "User registration is successful!";
    }
    @GetMapping("/profile-info")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserProfileInformation(){

       return userService.getUserProfileInformation();
    }
}
