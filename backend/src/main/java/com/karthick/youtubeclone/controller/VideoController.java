package com.karthick.youtubeclone.controller;

import com.karthick.youtubeclone.service.VideoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("")
@RequestMapping("${spring.services.path}/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestParam("file") MultipartFile file){

        videoService.uploadFile(file);
    }


}
