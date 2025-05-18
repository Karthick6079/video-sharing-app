package com.karthick.videosharingapp.exceptions;

import com.karthick.videosharingapp.domain.StatusResponse;
import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -2452850073078029330L;

    private StatusResponse statusResponse;

    private HttpStatus httpStatus;


    private String message;

    public ServiceException(){
        this.initiateBusinessException(null);
    }

    public ServiceException(String message) {

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
