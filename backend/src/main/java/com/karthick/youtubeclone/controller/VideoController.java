package com.karthick.youtubeclone.controller;

import com.karthick.youtubeclone.dto.UploadVideoResponse;
import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${spring.services.path}/video")
@RequiredArgsConstructor
@CrossOrigin
public class VideoController {

    private final VideoService videoService;



    @PostMapping(path = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse upload(@RequestParam("file") MultipartFile file){
        return videoService.uploadFile(file);
    }

    @PostMapping(path = "/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadThumbnail(@RequestParam("file") MultipartFile file, @RequestParam String videoId){
       return videoService.uploadThumbnail(file, videoId);
    }

    @PutMapping("/editMetadata")
    @ResponseStatus(HttpStatus.OK)
    public VideoDTO editVideoMetaData(@RequestBody VideoDTO videoDto){
      return videoService.editVideoMetaData(videoDto);
    }

    @GetMapping("/watch/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public VideoDTO getVideoForWatch(@PathVariable String videoId){
        return videoService.getVideo(videoId);
    }

    @PutMapping("/watch/{videoId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoDTO likeVideo(@PathVariable String videoId){
        return videoService.likeVideo(videoId);
    }


    @PutMapping("/watch/{videoId}/dislike")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoDTO dislikeVideo(@PathVariable String videoId){
        return videoService.dislikeVideo(videoId);
    }


    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDTO> getAllVideos(){
        return videoService.getAllVideos();
    }

    @GetMapping("/suggestion-videos")
    @ResponseStatus(HttpStatus.OK)
    public List<VideoDTO> getSuggestedVideos(@RequestParam( value = "page", defaultValue = "0" ) int page,
                                             @RequestParam( value = "size", defaultValue = "6") int size){

        return videoService.getSuggestionVideos(page, size);
    }

    @GetMapping("/short-video")
    public List<VideoDTO> getShortsVideo(){
        return videoService.getShortVideo();
    }


}
