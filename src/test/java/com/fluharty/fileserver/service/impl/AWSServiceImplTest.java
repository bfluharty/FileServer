package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AWSServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private AWSServiceImpl awsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";
        Long contentLength = 100L;
        String contentType = "text/plain";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        awsService.uploadFile(bucketName, keyName, contentLength, contentType, inputStream);

        ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);
        verify(s3Client, times(1)).putObject(eq(bucketName), eq(keyName), eq(inputStream), metadataCaptor.capture());

        ObjectMetadata metadata = metadataCaptor.getValue();
        assertEquals(contentLength, metadata.getContentLength());
        assertEquals(contentType, metadata.getContentType());
    }

    @Test
    void testDownloadFile() throws IOException {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(new ByteArrayInputStream("test content".getBytes()), null);

        when(s3Client.getObject(bucketName, keyName)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);

        ByteArrayOutputStream outputStream = awsService.downloadFile(bucketName, keyName);

        assertEquals("test content", outputStream.toString());
    }

    @Test
    void testListFiles() {
        String bucketName = "test-bucket";
        List<S3ObjectSummary> objectSummaries = new ArrayList<>();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey("test-file.txt");
        objectSummaries.add(summary);

        ObjectListing objectListing = mock(ObjectListing.class);
        when(objectListing.getObjectSummaries()).thenReturn(objectSummaries);
        when(objectListing.isTruncated()).thenReturn(false);
        when(s3Client.listNextBatchOfObjects(objectListing)).thenReturn(objectListing);
        when(s3Client.listObjects(bucketName)).thenReturn(objectListing);

        List<String> files = awsService.listFiles(bucketName);

        assertEquals(1, files.size());
        assertEquals("test-file.txt", files.get(0));
    }

    @Test
    void testDeleteFile() {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";

        awsService.deleteFile(bucketName, keyName);

        verify(s3Client, times(1)).deleteObject(bucketName, keyName);
    }

    @Test
    void testUploadFileThrowsAmazonClientException() {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";
        Long contentLength = 100L;
        String contentType = "text/plain";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        doThrow(new AmazonClientException("AWS error")).when(s3Client).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));

        assertThrows(AmazonClientException.class, () -> {
            awsService.uploadFile(bucketName, keyName, contentLength, contentType, inputStream);
        });
    }

    @Test
    void testDownloadFileThrowsAmazonClientException() {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";

        when(s3Client.getObject(bucketName, keyName)).thenThrow(new AmazonClientException("AWS error"));

        assertThrows(AmazonClientException.class, () -> {
            awsService.downloadFile(bucketName, keyName);
        });
    }

    @Test
    void testListFilesThrowsAmazonClientException() {
        String bucketName = "test-bucket";

        when(s3Client.listObjects(bucketName)).thenThrow(new AmazonClientException("AWS error"));

        assertThrows(AmazonClientException.class, () -> {
            awsService.listFiles(bucketName);
        });
    }

    @Test
    void testDeleteFileThrowsAmazonClientException() {
        String bucketName = "test-bucket";
        String keyName = "test-file.txt";

        doThrow(new AmazonClientException("AWS error")).when(s3Client).deleteObject(bucketName, keyName);

        assertThrows(AmazonClientException.class, () -> {
            awsService.deleteFile(bucketName, keyName);
        });
    }
}