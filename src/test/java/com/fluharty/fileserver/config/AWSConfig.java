package com.fluharty.fileserver.config;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AWSConfigTest {
    private AWSConfig awsConfig;

    @BeforeEach
    void setUp() {
        awsConfig = new AWSConfig();
        ReflectionTestUtils.setField(awsConfig, "accessKey", "testAccessKey");
        ReflectionTestUtils.setField(awsConfig, "accessSecret", "testSecretKey");
        ReflectionTestUtils.setField(awsConfig, "region", "us-east-1");
    }

    @Test
    void testS3ClientCreation() {
        AmazonS3 s3Client = awsConfig.s3Client();
        assertNotNull(s3Client, "The AmazonS3 client should not be null");
    }
}