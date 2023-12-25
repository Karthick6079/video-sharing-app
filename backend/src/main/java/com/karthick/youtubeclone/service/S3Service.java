package com.karthick.youtubeclone.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service implements FileService{

    private final S3Template s3Template;

    private final String BUCKET_NAME = "youtube-clone-avk";

    @Override
    public String uploadFile(MultipartFile file) {
        // Upload a file to AWS S3 Service

        var extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        var key = UUID.randomUUID() + "." + extension;

        ObjectMetadata objectMetadata = new ObjectMetadata.Builder()
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();


        try {
            return s3Template.upload(BUCKET_NAME, key, file.getInputStream(), objectMetadata).getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
