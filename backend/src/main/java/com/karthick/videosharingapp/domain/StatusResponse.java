package com.karthick.videosharingapp.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusResponse {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd \'T\' HH:mm:ss.SSSZ";
    private String code;

    private String message;
    private final String timeStamp = new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date());
    private String error;
    private String errorText;


    public StatusResponse(String code, String message){
        this(code, message, null);
    }

    public StatusResponse(String code, String message, String errorText) {
        this.code = code;
        this.message = message;
        this.errorText = errorText;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing the object of class " + this.getClass().getSimpleName()+ "to JSON failed", e);
        }
    }
}
