package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final VideoService videoService;

    private final UserService userService;

    private final MapperUtil mapperUtil;

    private final ModelMapper mapper;

    public List<VideoDTO> getWatchedVideos(){
        User user = userService.getCurrentUser();
        List<String> videoIdList = user.getVideoHistory();
        List<Video> videosList = videoService.fetchWatchedVideos(videoIdList);
        return videoService.getVideosAndUser(videosList);

    }
}
