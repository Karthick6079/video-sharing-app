package com.karthick.videosharingapp.service;

import com.karthick.videosharingapp.domain.CompleteMultipartRequest;
import com.karthick.videosharingapp.domain.dto.UploadVideoResponse;
import com.karthick.videosharingapp.entity.Video;
import com.karthick.videosharingapp.exceptions.AWSUploadException;
import com.karthick.videosharingapp.interfaces.MultiPartUploadService;
import com.karthick.videosharingapp.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class S3MultiPartUploadService implements MultiPartUploadService {

    public static final String THUMBNAILS = "thumbnails/";
    private final String BUCKET_VIDEO_FOLDER = "videos/";

    private final Logger logger = LoggerFactory.getLogger(S3MultiPartUploadService.class);

    @Value("${spring.aws-s3.bucket-name}")
    private String BUCKET_NAME;

    @Value("${spring.cloud.aws.region}")
    private String REGION;

    @Autowired
    S3Client s3Client;

    @Autowired
    S3Presigner s3Presigner;

    @Autowired
    VideoRepository videoRepository;


    @Override
    public Map<String, Object> initiateUpload(String filename) throws AWSUploadException {

        String uniqueId = String.valueOf(UUID.randomUUID());
        String key = BUCKET_VIDEO_FOLDER + uniqueId;

        logger.info("key {} generated for filename: {}", key, filename);

        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME).key(key).acl(ObjectCannedACL.PUBLIC_READ).build();
        CreateMultipartUploadResponse response;
        try {
            logger.info("initiating multipart upload in S3");
            //Null pointer exception simulation code
//            String str = null;
//            str.indexOf('w');
            response = s3Client.createMultipartUpload(request);
        } catch (Exception ex) {
            logger.error("Exception occurred during  multipart upload initiate", ex);
            throw new AWSUploadException("Exception occurred during  multipart upload initiate");
        }


        Map<String, Object> responseEntity = new HashMap<>();

        responseEntity.put("key", key);
        responseEntity.put("uploadId", response.uploadId());
        logger.info("Multipart upload initiated and returning uploadId to frontend");
        return responseEntity;
    }

    @Override
    public String generatePreSignedUrl(String key, String uploadId, int partNumber) {

        String url = "";

        UploadPartRequest request = UploadPartRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();
        logger.info("UploadpartRequest prepared");

        PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(builder ->
                builder.uploadPartRequest(request)
                        .signatureDuration(Duration.ofMinutes(15))
                        .build());
        url = presignedUploadPartRequest.url().toString();
        logger.info("presignedUploadPartRequest prepared, The url is {}", url);
        return url;
    }

    @Override
    public UploadVideoResponse completeUpload(CompleteMultipartRequest request) {
        logger.info("Complete Multipart upload process initiated");
        List<CompletedPart> completedPartList = request.getParts().stream()
                .map(p -> CompletedPart.builder()
                        .partNumber(p.getPartNumber())
                        .eTag(p.getEntityTag())
                        .build())
                .sorted(Comparator.comparingInt(CompletedPart::partNumber))
                .toList();

        completedPartList.forEach(part -> logger.info("PartNumber: {}, eTag: {}", part.partNumber(), part.eTag()));

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder().
                parts(completedPartList).build();

        CompleteMultipartUploadRequest completedMultipartRequest = CompleteMultipartUploadRequest.builder()
                .bucket(BUCKET_NAME)
                .key(request.getKey())
                .uploadId(request.getUploadId())
                .multipartUpload(completedMultipartUpload)
                .build();

        try {
            s3Client.completeMultipartUpload(completedMultipartRequest);
            logger.info("Complete Multipart upload process Completed successfully!!");



            String url = getS3FileUrl(request.getKey());
            Video video = new Video();
            video.setVideoUrl(url);
            logger.info("The video cloud url stored in database");
            Video savedVideo = videoRepository.save(video);
            logger.info("The video cloud url sent back to frontend");
            return new UploadVideoResponse(url, savedVideo.getId());
        } catch (Exception ex) {
            try {
                AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(request.getKey())
                        .uploadId(request.getUploadId())
                        .build();

                s3Client.abortMultipartUpload(abortRequest);
            } catch (Exception abortEx) {
                // Log both exceptions if abort also fails
                ex.addSuppressed(abortEx);
            }
            logger.error("Upload failed and was aborted: {}", ex.getMessage(), ex);
            throw new AWSUploadException("Upload failed and was aborted: " + ex.getMessage());
        }
    }

    public String getS3FileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", BUCKET_NAME, REGION, key);
    }

    public void generateThumbnail(Video video){
        String uniqueId = getKeyFromVideoUrl(video.getVideoUrl());
        String key = BUCKET_VIDEO_FOLDER + uniqueId + ".mp4";
        String outputKey = THUMBNAILS + uniqueId + ".jpg";
        generateThumbnail(key, outputKey);
    }

    private void generateThumbnail(String key, String outputKey) {
        File thumbnail = null;
        try {
            URL presignedUrl = generatePresignedUrl(BUCKET_NAME, key, Duration.ofMinutes(5));

            thumbnail = File.createTempFile("thumb", ".jpg");

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg",
                    "-ss", "00:00:03",
                    "-i", presignedUrl.toString(),
                    "-vframes", "1",
                    "-q:v", "2",
                    "-y",
                    thumbnail.getAbsolutePath()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                logger.error("Exception occurred FFmpeg process");
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }

            // Upload thumbnail to S3 using S3AsyncClient or S3Client
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(outputKey)
                    .contentType("image/jpeg")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(thumbnail));


        } catch (Exception e) {
            logger.error("Exception occurred during default thumbnail generation", e);
            throw new RuntimeException("Thumbnail generation failed", e);
        }
        finally {
            if(thumbnail != null)
                thumbnail.delete();

        }
    }

    private URL generatePresignedUrl(String bucketName, String key, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(expiration)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url(); // ✅ This is the presigned HTTPS URL
    }


    private String getBaseName(String key) {
        return key.substring(key.lastIndexOf('/') + 1).replace(".mp4", "");
    }

    private  String getKeyFromVideoUrl(String videoUrl){
        int index = videoUrl.lastIndexOf("/") + 1;
        return videoUrl.substring(index).replace(".mp4", "");
    }


}

