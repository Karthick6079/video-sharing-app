package com.karthick.youtubeclone.interfaces;

import com.karthick.youtubeclone.domain.CompleteMultipartRequest;
import com.karthick.youtubeclone.dto.UploadVideoResponse;
import com.karthick.youtubeclone.exceptions.AWSUploadException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public interface MultiPartUploadService {




    Map<String, Object> initiateUpload(String filename) throws AWSUploadException;

    String generatePreSignedUrl(String key, String uploadId, int partNumber);

    UploadVideoResponse completeUpload(CompleteMultipartRequest request);




}