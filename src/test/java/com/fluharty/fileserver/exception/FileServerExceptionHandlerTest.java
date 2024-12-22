package com.fluharty.fileserver.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileServerExceptionHandlerTest {

    private FileServerExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new FileServerExceptionHandler();
    }

    @Test
    void testHandleFileServerException_WithCustomStatus() {
        // Arrange
        String explanation = "Custom explanation";
        String message = "Custom message";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        FileServerException exception = new FileServerException(explanation, message, status);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleFileServiceException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        assertEquals("{" +
                "\"explanation\":\"" + explanation + "\"," +
                "\"message\":\"" + message + "\"" +
                "}", response.getBody(), "Response body should contain the exception details");
    }

    @Test
    void testHandleFileServerException_WithoutCustomStatus() {
        // Arrange
        String explanation = "Default explanation";
        String message = "Default message";
        FileServerException exception = new FileServerException(explanation, message, null);  // No status provided

        // Act
        ResponseEntity<String> response = exceptionHandler.handleFileServiceException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR when no status is provided");
        assertEquals("{" +
                "\"explanation\":\"" + explanation + "\"," +
                "\"message\":\"" + message + "\"" +
                "}", response.getBody(), "Response body should contain the exception details");
    }
}