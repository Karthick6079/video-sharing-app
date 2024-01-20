package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.dto.UploadVideoResponse;
import com.karthick.youtubeclone.dto.UserDTO;
import com.karthick.youtubeclone.dto.VideoDTO;
import com.karthick.youtubeclone.entity.User;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.repository.VideoRepository;
import com.karthick.youtubeclone.servicelogic.VideoServiceLogic;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;

    private final VideoRepository videoRepository;

    private final ModelMapper mapper;

    private final VideoServiceLogic videoServiceLogic;

    private final UserService userService;


    public UploadVideoResponse uploadFile(MultipartFile file) {
        String url = s3Service.uploadFile(file);

        Video video = new Video();
        video.setVideoUrl(url);

        Video savedVideo =  videoRepository.save(video);

        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public UploadVideoResponse uploadThumbnail(MultipartFile file, String videoId) {
        String url = s3Service.uploadFile(file);

        Video savedVideo = findVideoById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + videoId));
        savedVideo.setThumbnailUrl(url);

        videoRepository.save(savedVideo);

        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public VideoDTO editVideoMetaData(VideoDTO videoDto) {
        Video savedVideo = getVideoFromDB(videoDto.getId());

        User currerntUser = userService.getCurrentUser();

        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setPublishedDateAndTime(LocalDateTime.now());
        savedVideo.setUserId(currerntUser.getId());
//        savedVideo.setUser(currerntUser);

        //update video url to video dto
        videoDto.setVideoUrl(savedVideo.getVideoUrl());

        videoRepository.save(savedVideo);

        return videoDto;

    }

    public Optional<Video> findVideoById(String id) {
        return videoRepository.findById(id);
    }


    private Video getVideoFromDB(String id){
        return findVideoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + id));
    }

    public VideoDTO getVideo(String videoId) {
        Video video = getVideoFromDB(videoId);

        video.increaseViewCount();
        userService.addToWatchHistory(videoId);
        // Get channel information about video
        User videoUploadedUser =  userService.getUserById(video.getUserId());
        UserDTO userDTO = mapper.map(videoUploadedUser, UserDTO.class);


        videoRepository.save(video);
        VideoDTO videoDTO = mapper.map(video, VideoDTO.class);
        videoDTO.setUserDTO(userDTO);

        return videoDTO;

    }

    public VideoDTO likeVideo(String videoId) {

        // Get Video and user entity from DB
        Video video = getVideoFromDB(videoId);
        User user = userService.getCurrentUser();

        //Updating like count based on different scenario
        videoServiceLogic.likeVideo(video, user,videoId);

        // After manipulating likes count and liked video list
        // Save video and user entity to database
        videoRepository.save(video);
        userService.saveUser(user);

        return mapper.map(video, VideoDTO.class);


    }
    public VideoDTO dislikeVideo(String videoId) {

        // Get Video and user entity from DB
        Video video = getVideoFromDB(videoId);
        User user = userService.getCurrentUser();

        //Updating dislike count based on different scenario
        videoServiceLogic.dislikeVideo(video, user,videoId);

        // After manipulating dislikes count and disliked video list
        // Save video and user entity to database
        videoRepository.save(video);
        userService.saveUser(user);

        return mapper.map(video, VideoDTO.class);


    }



    public List<VideoDTO> getVideosAndUser(List<Video> videos) {
        return videos.stream().map(video -> {
//            mapper.getConfiguration().getMatchingStrategy().
            VideoDTO videoDTO = mapper.map(video, VideoDTO.class);
            Long publishedInLong = video.getPublishedDateAndTime()
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            videoDTO.setPublishedDateAndTime(publishedInLong);
            if(video.getUserId() != null){
                User user = userService.getUserById(video.getUserId());
                UserDTO userDTO = userService.convertUsertoUserDto(user, mapper);
                videoDTO.setUserDTO(userDTO);
            }
            return videoDTO;
        }).toList();
    }

    public List<VideoDTO> getAllVideos(){

        List<Video> videos;
        videos = (List<Video>) videoRepository.findAll(PageRequest.of(0,12)).toList();

//        Collections.shuffle(videos);

        return getVideosAndUser(videos);
    }




    public List<Video> fetchWatchedVideos(List<String> videoIdList) {
        return videoRepository.findAllById(videoIdList);
    }
}
