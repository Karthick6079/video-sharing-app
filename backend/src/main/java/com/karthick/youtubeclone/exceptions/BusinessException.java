package com.karthick.youtubeclone.exceptions;

import com.karthick.youtubeclone.domain.StatusResponse;
import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{


    private static final long serialVersionUID = -6330874651123311837L;

    private StatusResponse statusResponse;

    private HttpStatus httpStatus;

    private String message;

    public BusinessException(){
        this.initiateBusinessException(null);
    }

    public BusinessException(String message) {

        this.message = message;
        this.initiateBusinessException(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private void initiateBusinessException(String message){
        ServiceExceptionValues businessException = null;
        if(ServiceExceptionValues.contains(getClass().getSimpleName())) {
            businessException = ServiceExceptionValues.valueOf(getClass().getSimpleName());
        } else {
            businessException = ServiceExceptionValues.UnknownException;
        }

        String messageResponse = message != null ? message: businessException.getMessage();
        this.httpStatus = businessException.getHttpStatus();
        this.statusResponse = new StatusResponse(businessException.getCode(), messageResponse, businessException.getErrorText());


    }

    public void setMessage(String message) {
        this.message = message;
    }

    public StatusResponse getStatusResponse() {
        return statusResponse;
    }

    public void setStatusResponse(StatusResponse statusResponse) {
        this.statusResponse = statusResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
