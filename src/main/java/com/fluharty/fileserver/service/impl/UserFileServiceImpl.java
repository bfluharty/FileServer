package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.fluharty.fileserver.exception.FileServerException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import com.fluharty.fileserver.service.AWSService;
import com.fluharty.fileserver.service.UserFileService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.fluharty.fileserver.utils.Constants.*;

@Service
public class UserFileServiceImpl implements UserFileService {
    private static final Logger log = LoggerFactory.getLogger(UserFileServiceImpl.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final UserFilesRepository userFilesRepository;
    private final AWSService awsService;

    private final ConcurrentHashMap<String, List<String>> listFilesCache = new ConcurrentHashMap<>();

    public UserFileServiceImpl(UserFilesRepository userFilesRepository, AWSService awsService) {
        this.userFilesRepository = userFilesRepository;
        this.awsService = awsService;
    }

    public List<String> getFiles() {
        log.debug("Retrieving files from cache");
        return listFilesCache.computeIfAbsent(bucketName, key -> {
            try {
                log.debug("Retrieving files from AWS S3 bucket: {}", bucketName);
                return awsService.listFiles(bucketName);
            } catch (AmazonClientException e) {
                log.error("Failed to retrieve files from AWS S3 bucket: {}", e.getMessage());
                throw new FileServerException(LIST_FILES, e.getMessage(), null);
            }
        });
    }

    @Transactional
    public void upload(UserFile userFile, MultipartFile file) {
//        userFilesRepository.save(userFile);
        try {
            log.debug("Uploading file to AWS S3 bucket: {}", file.getOriginalFilename());
            awsService.uploadFile(bucketName, file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getInputStream());
            refreshCacheAsync();
        } catch (AmazonClientException | IOException e) {
            log.error("Failed to upload file to AWS S3 bucket: {}", e.getMessage());
            throw new FileServerException(FILE_UPLOAD, e.getMessage(), null);
        }
    }

    public byte[] download(String filename) {
        try {
            log.debug("Downloading file from AWS S3 bucket: {}", filename);
            return awsService.downloadFile(bucketName, filename).toByteArray();
        } catch (AmazonClientException | IOException e) {
            log.error("Failed to download file from AWS S3 bucket: {}", e.getMessage());
            throw new FileServerException(FILE_DOWNLOAD, e.getMessage(), null);
        }
    }

    public void delete(String filename) {
        try {
            log.debug("Deleting file from AWS S3 bucket: {}", filename);
            awsService.deleteFile(bucketName, filename);
            refreshCacheAsync();
        } catch (AmazonClientException e) {
            log.error("Failed to delete file from AWS S3 bucket: {}", e.getMessage());
            throw new FileServerException(FILE_DELETION, e.getMessage(), null);
        }
    }

    private void refreshCacheAsync() {
        new Thread(() -> {
            try {
                log.debug("Refreshing file list cache");
                List<String> files = awsService.listFiles(bucketName);
                listFilesCache.put(bucketName, files);
            } catch (AmazonClientException e) {
                log.error("Failed to refresh file list cache: {}", e.getMessage());
            }
        }).start();
    }
}
