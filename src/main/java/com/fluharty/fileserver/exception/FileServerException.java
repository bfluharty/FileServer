package com.fluharty.fileserver.exception;

import org.springframework.http.HttpStatus;

public class FileServerException extends RuntimeException {
    private final HttpStatus status;
    private final String explanation;
    private final String message;

    public FileServerException(String explanation, String message, HttpStatus status) {
        this.status = status;
        this.explanation = explanation;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "{" +
                "\"explanation\":\"" + explanation + "\"," +
                "\"message\":\"" + message + "\"" +
                "}";
    }
}