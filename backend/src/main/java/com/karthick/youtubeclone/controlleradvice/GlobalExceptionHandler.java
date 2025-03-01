package com.karthick.youtubeclone.controlleradvice;

import com.karthick.youtubeclone.domain.StatusResponse;
import com.karthick.youtubeclone.exceptions.BusinessException;
import com.karthick.youtubeclone.exceptions.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.http.HttpResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StatusResponse> handleBusinessException(BusinessException ex){
        System.out.println("The business Exception occurred! Http status is internal server error");
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<StatusResponse> handleServiceException(ServiceException ex){
        System.out.println("The service Exception occurred! Http status is internal server error");
        return ResponseEntity.internalServerError().body(ex.getStatusResponse());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception exception, HttpServletResponse response) throws IOException {
        response.sendError(500, exception.getMessage());
    }
}
