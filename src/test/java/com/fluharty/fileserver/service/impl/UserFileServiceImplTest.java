package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.fluharty.fileserver.exception.FileServerException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import com.fluharty.fileserver.service.AWSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.fluharty.fileserver.utils.Constants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserFileServiceImplTest {

    @Mock
    private AWSService awsService;

    @Mock
    private UserFilesRepository userFilesRepository;

    @InjectMocks
    private UserFileServiceImpl userFileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userFileService, "bucketName", "testBucket");
    }

    // Happy Path: Test successful file retrieval from cache and AWS S3
    @Test
    void testGetFilesSuccess() {
        List<String> mockFileList = List.of("file1.txt", "file2.txt");
        when(awsService.listFiles(anyString())).thenReturn(mockFileList);

        List<String> files = userFileService.getFiles();

        assertEquals(2, files.size());
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.txt"));
        verify(awsService, times(1)).listFiles(anyString());
    }

    // Error Path: Test exception handling when AWS S3 fails to retrieve files
    @Test
    void testGetFilesAwsException() {
        when(awsService.listFiles(anyString())).thenThrow(new AmazonClientException("AWS error"));

        FileServerException exception = assertThrows(FileServerException.class, () -> {
            userFileService.getFiles();
        });

        assertTrue(exception.toString().contains(LIST_FILES));
        verify(awsService, times(1)).listFiles(anyString());
    }

    // Happy Path: Test successful file upload
    @Test
    void testUploadFileSuccess() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file1.txt");
        when(file.getSize()).thenReturn(100L);
        when(file.getContentType()).thenReturn("text/plain");

        doNothing().when(awsService).uploadFile(anyString(), anyString(), anyLong(), anyString(), any());

        userFileService.upload(new UserFile("testUser", "file1.txt", null), file);

        verify(awsService, times(1)).uploadFile(anyString(), anyString(), anyLong(), anyString(), any());
    }

    // Error Path: Test exception handling during file upload
    @Test
    void testUploadFileAwsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file1.txt");
        when(file.getSize()).thenReturn(100L);
        when(file.getContentType()).thenReturn("text/plain");

        doThrow(new AmazonClientException("AWS error")).when(awsService).uploadFile(anyString(), anyString(), anyLong(), anyString(), any());

        FileServerException exception = assertThrows(FileServerException.class, () -> {
            userFileService.upload(new UserFile("testUser", "file1.txt", null), file);
        });

        assertTrue(exception.toString().contains(FILE_UPLOAD));
        verify(awsService, times(1)).uploadFile(anyString(), anyString(), anyLong(), anyString(), any());
    }

    // Happy Path: Test file download
    @Test
    void testDownloadFileSuccess() throws IOException {
        String filename = "file1.txt";
        byte[] mockFileData = "file content".getBytes();
        when(awsService.downloadFile(anyString(), eq(filename))).thenReturn(new ByteArrayOutputStream());

        byte[] downloadedData = userFileService.download(filename);

        assertNotNull(downloadedData);
        verify(awsService, times(1)).downloadFile(anyString(), eq(filename));
    }

    // Error Path: Test exception handling during file download
    @Test
    void testDownloadFileAwsException() throws IOException {
        String filename = "file1.txt";
        when(awsService.downloadFile(anyString(), eq(filename))).thenThrow(new AmazonClientException("AWS error"));

        FileServerException exception = assertThrows(FileServerException.class, () -> {
            userFileService.download(filename);
        });

        assertTrue(exception.toString().contains(FILE_DOWNLOAD));
        verify(awsService, times(1)).downloadFile(anyString(), eq(filename));
    }

    // Happy Path: Test successful file deletion
    @Test
    void testDeleteFileSuccess() {
        String filename = "file1.txt";
        doNothing().when(awsService).deleteFile(anyString(), eq(filename));

        userFileService.delete(filename);

        verify(awsService, times(1)).deleteFile(anyString(), eq(filename));
    }

    // Error Path: Test exception handling during file deletion
    @Test
    void testDeleteFileAwsException() {
        String filename = "file1.txt";
        doThrow(new AmazonClientException("AWS error")).when(awsService).deleteFile(anyString(), eq(filename));

        FileServerException exception = assertThrows(FileServerException.class, () -> {
            userFileService.delete(filename);
        });

        assertTrue(exception.toString().contains(FILE_DELETION));
        verify(awsService, times(1)).deleteFile(anyString(), eq(filename));
    }

    // Edge Case: Test empty file list returned from cache
    @Test
    void testGetFilesEmptyList() {
        when(awsService.listFiles(anyString())).thenReturn(List.of());

        List<String> files = userFileService.getFiles();

        assertTrue(files.isEmpty());
        verify(awsService, times(1)).listFiles(anyString());
    }

    @Test
    void testGetBucketSize() {
        when(awsService.getBucketSize(anyString())).thenReturn(100L);

        long size = userFileService.getBucketSize();

        assertEquals(100L, size);
    }

    @Test
    void testGetBucketSizeThrowsAmazonClientException() {
        when(awsService.getBucketSize(anyString())).thenThrow(new AmazonClientException("AWS error"));

        assertThrows(FileServerException.class, () -> {
            userFileService.getBucketSize();
        });
    }

}
