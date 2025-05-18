package com.karthick.videosharingapp.exceptions;

import org.springframework.http.HttpStatus;

import java.util.HashSet;

public enum ServiceExceptionValues {

    BusinessException("10010","Business exception an occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    ServiceException("10011","Service exception an occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    FileSizeExceededException("10012", "File size exceeded the limit", HttpStatus.INTERNAL_SERVER_ERROR),
    AWSUploadException("10013", "Exception occurred during uploading to AWS!", HttpStatus.INTERNAL_SERVER_ERROR),
    UnknownException("10000","Unknown Service exception an occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;

    private final String message;

    private final String errorText;

    private final HttpStatus httpStatus;

    private ServiceExceptionValues(String code, String message){
        this(code, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ServiceExceptionValues(String code, String message, HttpStatus httpStatus) {
        this(code, message, httpStatus, null);
    }

    private ServiceExceptionValues(String code, String message, HttpStatus httpStatus, String errorText) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorText = errorText;
    }

    public String getCode() {
        return code;
    }

    public String getErrorText() {
        return errorText;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public static boolean contains(String classname){
        HashSet<String> hashset = new HashSet<>();
        for(ServiceExceptionValues value: ServiceExceptionValues.values()){
            hashset.add(value.getClass().getName());
        }

        return hashset.contains(classname);
    }
}
