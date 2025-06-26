package com.karthick.videosharingapp.interfaces;

import com.karthick.videosharingapp.domain.CompleteMultipartRequest;
import com.karthick.videosharingapp.domain.dto.UploadVideoResponse;
import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.exceptions.AWSUploadException;

import java.util.Map;

public interface MultiPartUploadService {




    Map<String, Object> initiateUpload(String filename, String fileExtension) throws AWSUploadException;

    String generatePreSignedUrl(String key, String uploadId, int partNumber);

    UploadVideoResponse completeUpload(CompleteMultipartRequest request);

   String generateThumbnail(Video video);




}