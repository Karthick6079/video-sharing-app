package com.karthick.videosharingapp.controlleradvice;

import com.karthick.videosharingapp.domain.StatusResponse;
import com.karthick.videosharingapp.exceptions.BusinessException;
import com.karthick.videosharingapp.exceptions.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StatusResponse> handleBusinessException(BusinessException ex){
        logger.error("The business Exception occurred! Http status is internal server error", ex);
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<StatusResponse> handleServiceException(ServiceException ex){
        logger.error("The service Exception occurred! Http status is internal server error", ex);
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusResponse> handleException(Exception exception, HttpServletResponse response) throws IOException {
        logger.error("An unhandled exception occurred:", exception);
        return ResponseEntity.internalServerError().body(new StatusResponse("JAVA_ERR_UNHANDLED", exception.getMessage()));
    }
}
