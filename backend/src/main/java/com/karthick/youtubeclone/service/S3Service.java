package com.karthick.youtubeclone.service;

import com.karthick.youtubeclone.controller.VideoController;
import com.karthick.youtubeclone.exceptions.AWSUploadException;
import com.karthick.youtubeclone.exceptions.ServiceException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service implements FileService{
    @Autowired
    private  S3Template s3Template;

    @Value("${spring.aws-s3.bucket-name}")
    private  String BUCKET_NAME;

    private final Logger logger = LoggerFactory.getLogger(S3Service.class);


    @Override
    public String uploadFile(MultipartFile file) {

        // Upload a file to AWS S3 Service
        var extension = StringUtils.getFilenameExtension(file.getOriginalFilename());

        var key = UUID.randomUUID() + "." + extension;
        logger.info("The unique file name generated {} for actual file {}", key, file.getName());

        logger.info("Metadata prepared for this file");
        ObjectMetadata objectMetadata = new ObjectMetadata.Builder()
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        try {
            logger.info("Uploading a file into S3 using S3Template");
            return s3Template.upload(BUCKET_NAME, key, file.getInputStream(), objectMetadata).getURL().toString();
        } catch (Exception ex) {
            throw new AWSUploadException("Exception occurred during uploading to AWS!");
        }

    }
}
