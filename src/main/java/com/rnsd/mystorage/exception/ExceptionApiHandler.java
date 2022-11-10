package com.rnsd.mystorage.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionApiHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException customException) {
        log.error(customException.getMessage(), customException);
        return ResponseEntity
                .status(customException.getStatus())
                .body(new ErrorResponse(customException.getMessage()));
    }
}
