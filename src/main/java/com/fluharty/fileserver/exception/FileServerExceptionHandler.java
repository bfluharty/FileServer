package com.fluharty.fileserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileServerExceptionHandler {
    @ExceptionHandler(FileServerException.class)
    public ResponseEntity<String> handleFileServiceException(FileServerException ex) {
        return new ResponseEntity<>(ex.toString(),
                ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}