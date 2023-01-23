package com.dayone.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException abstractException){
        ErrorResponse e = ErrorResponse.builder()
            .code(abstractException.getStatusCode())
            .message(abstractException.getMessage())
            .build();
        return new ResponseEntity<>(e, HttpStatus.resolve(abstractException.getStatusCode()));
    }

}
