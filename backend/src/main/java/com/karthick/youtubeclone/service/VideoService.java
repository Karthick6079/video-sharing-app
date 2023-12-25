package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.dto.UploadVideoResponse;
import com.karthick.youtubeclone.dto.VideoDto;
import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;

    private final VideoRepository videoRepository;

    public UploadVideoResponse uploadFile(MultipartFile file) {
        String url = s3Service.uploadFile(file);

        Video video = new Video();
        video.setVideoUrl(url);

        Video savedVideo =  videoRepository.save(video);

        return new UploadVideoResponse(url, savedVideo.getId());

    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        String url = s3Service.uploadFile(file);

        Video savedVideo = savedVideo(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + videoId));
        savedVideo.setThumbnailUrl(url);

        videoRepository.save(savedVideo);

        return url;

    }

    public VideoDto editVideoMetaData(VideoDto videoDto) {
        Video savedVideo = savedVideo(videoDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by Id" + videoDto.getId()));

        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTitle(videoDto.getTitle());

        //update video url to video dto
        videoDto.setVideoUrl(savedVideo.getVideoUrl());

        videoRepository.save(savedVideo);

        return videoDto;

    }

    public Optional<Video> savedVideo(String id) {
        return videoRepository.findById(id);
    }
}
