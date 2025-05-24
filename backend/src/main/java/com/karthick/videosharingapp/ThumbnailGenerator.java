package com.karthick.videosharingapp;

import com.karthick.videosharingapp.entity.Video;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@Profile("thumbnail-generate")
public class ThumbnailGenerator  implements CommandLineRunner {


    private final Logger logger = LoggerFactory.getLogger(ThumbnailGenerator.class);

    private static final String SOURCE_BUCKET = "amzn-s3-videostreaming-app-bucket";
    private static final String DEST_BUCKET = "amzn-s3-videostreaming-app-bucket";
    private static final String SOURCE_PREFIX = "videos/";
    private static final String DEST_PREFIX = "thumbnails/";


    private final S3Client s3Client;
    private final MongoTemplate mongo;


    public ThumbnailGenerator(MongoTemplate mongo, S3Client s3Client) {
        this.mongo = mongo;
        this.s3Client = s3Client;
    }

    public void run(String... args) throws Exception {

        List<S3Object> videos = listVideoFiles(s3Client);
        for (S3Object video : videos) {
            String key = video.key();
            System.out.println("⏳ Processing: " + key);
            try {
                File videoFile = downloadFileFromS3(s3Client, key);
                File thumbnailFile = extractSmartThumbnail(videoFile);
                String thumbKey = key.replace(SOURCE_PREFIX, DEST_PREFIX).replaceAll("\\.mp4$", ".jpg");
                uploadFileToS3(s3Client, thumbKey, thumbnailFile);
                System.out.println("✅ Done: " + thumbKey);
            } catch (Exception e) {
                System.err.println("❌ Failed for " + key + ": " + e.getMessage());
            }
        }
    }


    private List<Video> getVideosFromDB(String collection){

        Query q = new Query().addCriteria(where("videoUrl").regex("\\"));
        List<Video> docs = null;
        try{
            docs = mongo.find(q, Video.class, collection);

            if (docs.isEmpty()) {
                logger.info("[Migration] Nothing to migrate, exiting. empty records: {}", collection);
                return docs;
            }

        } catch (Exception e){
            logger.error("Exception occurred during mongo db collection: {} data fetch", collection, e);
        }

        return docs;
    }

    private static List<S3Object> listVideoFiles(S3Client s3) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(SOURCE_BUCKET)
                .prefix(SOURCE_PREFIX)
                .build();
        ListObjectsV2Response response = s3.listObjectsV2(request);
        return response.contents().stream()
                .filter(obj -> obj.key().endsWith(".mp4"))
                .collect(Collectors.toList());
    }

    private static File downloadFileFromS3(S3Client s3, String key) throws IOException {
        File tempFile = File.createTempFile("video", ".mp4");
        try (ResponseInputStream<GetObjectResponse> inputStream =
                     s3.getObject(GetObjectRequest.builder().bucket(SOURCE_BUCKET).key(key).build())) {
            FileUtils.copyInputStreamToFile(inputStream, tempFile);
        }
        return tempFile;
    }

    private static File extractSmartThumbnail(File videoFile) throws IOException, InterruptedException {
        File thumbFile = File.createTempFile("thumbnail", ".jpg");
        String[] cmd = {
                "ffmpeg",
                "-i", videoFile.getAbsolutePath(),
                "-vf", "select='gt(scene,0.3)',scale=480:-1",
                "-frames:v", "1",
                "-q:v", "2",
                thumbFile.getAbsolutePath()
        };
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new RuntimeException("FFmpeg failed. Exit code: " + exitCode);
        return thumbFile;
    }

    private static void uploadFileToS3(S3Client s3, String key, File file) throws IOException {
        s3.putObject(PutObjectRequest.builder()
                        .bucket(DEST_BUCKET)
                        .key(key)
                        .contentType("image/jpeg")
                        .acl(ObjectCannedACL.PUBLIC_READ) // Optional
                        .build(),
                RequestBody.fromBytes(Files.readAllBytes(file.toPath())));
    }
}
