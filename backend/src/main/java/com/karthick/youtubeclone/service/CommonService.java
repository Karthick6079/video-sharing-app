package com.karthick.youtubeclone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.youtubeclone.dto.LikedVideoDTO;
import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.dto.WatchedVideoDTO;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.entity.WatchedVideo;
import com.karthick.youtubeclone.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
