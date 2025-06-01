package com.karthick.videosharingapp;

import com.karthick.videosharingapp.entity.Video;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;
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
    private final MongoTemplate mongoTemplate;


    public ThumbnailGenerator(MongoTemplate mongoTemplate, S3Client s3Client) {
        this.mongoTemplate = mongoTemplate;
        this.s3Client = s3Client;
    }

    public void run(String... args) throws Exception {

        List<S3Object> videos = listVideoFiles(s3Client);
//        List<S3Object> videoStreamedList = videos.stream().limit(1).toList();
        logger.info("No of S3 objects fetched for Iteration is : {}", videos.size());
        for (S3Object video : videos) {
            String key = video.key();
            if(key.contains("06f1c264-2bdf-4cf9-8649-cf141a05ddb3"))
                continue; // since it is alread processed
//            key = key.replace("videos/","");
            logger.info("⏳ Processing: {}",  key);
            File videoFile = null;
            File thumbnailFile = null;
            try {
                videoFile = downloadFileFromS3(s3Client, key);
                thumbnailFile = extractSmartThumbnail(videoFile);
                String thumbKey = key.replace(SOURCE_PREFIX, DEST_PREFIX).replaceAll("\\.mp4$", ".jpg");
                uploadFileToS3(s3Client, thumbKey, thumbnailFile);
                logger.info("Thumbnail stored at: {}", thumbKey);
                updateVideosCollection(key, thumbKey);
                logger.info("✅ Done: {}" , thumbKey);
            } catch (Exception e) {
                logger.error("❌ Failed for {} : {}" , key , e.getMessage());
            } finally {
                if(videoFile != null)
                    logger.info("is Temp video file deleted? : {}", videoFile.delete());
                if(thumbnailFile != null)
                    logger.info("is Temp image file deleted? : {}", thumbnailFile.delete());
            }
        }
    }


    private void updateVideosCollection(String key, String thumbnailKey){

        String COLLECTION = "videos";
        String searchKey = key.replace("videos/","");
        Pattern pattern = Pattern.compile(searchKey);

        Query q = new Query().addCriteria(where("videoUrl").regex(pattern));
        List<Video> doc = null;
        try{
            doc = mongoTemplate.find(q, Video.class, COLLECTION);
            
//            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, collection);


            if (doc.isEmpty()) {
                logger.info("[Migration] Nothing to migrate, exiting. empty records: {}", COLLECTION);
                return;
            }

            String updatedVideoUrl = doc.get(0).getVideoUrl().replace(searchKey, key);
            String thumbnailUrl = doc.get(0).getVideoUrl().replace(searchKey, thumbnailKey);

            Update update = new Update();
            update.set("videoUrl", updatedVideoUrl);
            update.set("thumbnailUrl", thumbnailUrl);
            Query query = new Query(Criteria.where("_id").is(doc.get(0).getId()));
            
            mongoTemplate.updateFirst(query, update, Video.class);
            logger.info("The videoUrl and thumbnailUrl updated for video: {}", doc.get(0).getTitle());
            

        } catch (Exception e){
            logger.error("Exception occurred during mongo db collection: {} data fetch", COLLECTION, e);
        }
    }

    private List<S3Object> listVideoFiles(S3Client s3) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(SOURCE_BUCKET)
                .prefix(SOURCE_PREFIX)
                .build();
        ListObjectsV2Response response = s3.listObjectsV2(request);
        return response.contents().stream()
                .filter(obj -> obj.key().endsWith(".mp4"))
                .collect(Collectors.toList());
    }

    private File downloadFileFromS3(S3Client s3, String key) throws IOException {

        File tempFile = File.createTempFile("video", ".mp4");
        try (ResponseInputStream<GetObjectResponse> inputStream =
                     s3.getObject(GetObjectRequest.builder().bucket(SOURCE_BUCKET).key(key).build())) {
            logger.info("Object downloaded to S3 and copying the content local temp path");
            FileUtils.copyInputStreamToFile(inputStream, tempFile);
        }
        return tempFile;
    }

    private File extractSmartThumbnail(File videoFile) throws IOException, InterruptedException {
        File thumbFile = File.createTempFile("thumbnail", ".jpg");
        String[] cmd = {
                "ffmpeg",
                "-i", videoFile.getAbsolutePath(),
                "-vf", "select='gt(scene,0.3)',scale=480:-1",
                "-frames:v", "1",
                "-q:v", "2",
                "-y",
                thumbFile.getAbsolutePath()
        };
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        logger.info("Extracted the thumbnail from video and storing it temp path ");
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new RuntimeException("FFmpeg failed. Exit code: " + exitCode);
        return thumbFile;
    }

    private void uploadFileToS3(S3Client s3, String key, File file) throws IOException {
        logger.info("Uploading thumbnail to S3 with key: {}", key);
        s3.putObject(PutObjectRequest.builder()
                        .bucket(DEST_BUCKET)
                        .key(key)
                        .contentType("image/jpeg")
                        .acl(ObjectCannedACL.PUBLIC_READ) // Optional
                        .build(),
                RequestBody.fromBytes(Files.readAllBytes(file.toPath())));
    }
}
