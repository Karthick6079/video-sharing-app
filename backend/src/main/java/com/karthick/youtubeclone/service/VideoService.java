package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.entity.Video;
import com.karthick.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;

    private final VideoRepository videoRepository;

    public void uploadFile(MultipartFile file){
        String url = s3Service.uploadFile(file);

        System.out.println("========>" + url + "<======================");
        Video video = new Video();
        video.setVideoUrl(url);

        videoRepository.save(video);

    }
}
