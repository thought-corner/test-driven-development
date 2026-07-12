package com.study.tdd.api.exception;

import com.study.tdd.application.InvalidCommandException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidCommandExceptionHandler {

    @ExceptionHandler(InvalidCommandException.class)
    public ResponseEntity<?> handle() {
        return ResponseEntity.badRequest().build();
    }
}
