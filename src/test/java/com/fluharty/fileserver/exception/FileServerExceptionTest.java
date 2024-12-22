package com.fluharty.fileserver.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class FileServerExceptionTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String explanation = "Test explanation";
        String message = "Test message";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Act
        FileServerException exception = new FileServerException(explanation, message, status);

        // Assert
        assertNotNull(exception, "Exception should not be null");
        assertEquals(status, exception.getStatus(), "Status should match the provided value");
    }

    @Test
    void testToStringMethod() {
        // Arrange
        String explanation = "Test explanation";
        String message = "Test message";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        FileServerException exception = new FileServerException(explanation, message, status);

        // Act
        String result = exception.toString();

        // Assert
        assertNotNull(result, "The toString result should not be null");
        assertTrue(result.contains(explanation), "The explanation should be in the toString output");
        assertTrue(result.contains(message), "The message should be in the toString output");
        assertTrue(result.contains("\"explanation\":\"" + explanation + "\""), "The explanation should be correctly formatted");
        assertTrue(result.contains("\"message\":\"" + message + "\""), "The message should be correctly formatted");
    }
}