package com.karthick.youtubeclone.domain;

import lombok.Data;

import java.util.List;
@Data
public class CompleteMultipartRequest {
    private String key;
    private String uploadId;
    private List<PartETag> parts;

    @Data
    public static class PartETag {
        private int partNumber;
        private String entityTag;
    }

    // getters and setters
}

