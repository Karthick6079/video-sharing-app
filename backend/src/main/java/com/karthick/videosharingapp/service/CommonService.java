package com.karthick.videosharingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.videosharingapp.dto.LikedVideoDTO;
import com.karthick.videosharingapp.dto.WatchedVideoDTO;
import com.karthick.videosharingapp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final VideoService videoService;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    public List<WatchedVideoDTO> getWatchedVideos(int page, int size) throws JsonProcessingException {
        User user = userService.getCurrentUser();
//        Map<LocalDate, List<WatchedVideoDTO>> groupedObject = videoService.fetchWatchedVideos(user.getId(), page, size);
        return videoService.fetchWatchedVideos(user.getId(), page, size);
    }


    public List<LikedVideoDTO> getLikedVideos(int page, int size){
        return videoService.getLikedVideos(page, size);
    }



}
