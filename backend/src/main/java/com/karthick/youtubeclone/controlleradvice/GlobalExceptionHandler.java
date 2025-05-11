package com.karthick.youtubeclone.controlleradvice;

import com.karthick.youtubeclone.domain.StatusResponse;
import com.karthick.youtubeclone.exceptions.BusinessException;
import com.karthick.youtubeclone.exceptions.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.http.HttpResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StatusResponse> handleBusinessException(BusinessException ex){
        logger.info("The business Exception occurred! Http status is internal server error");
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<StatusResponse> handleServiceException(ServiceException ex){
        logger.info("The service Exception occurred! Http status is internal server error");
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception, HttpServletResponse response) throws IOException {
        response.sendError(500, exception.getMessage());
    }
}
