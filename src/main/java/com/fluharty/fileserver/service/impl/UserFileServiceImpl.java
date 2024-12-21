package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.fluharty.fileserver.exception.FileServerException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import com.fluharty.fileserver.service.AWSService;
import com.fluharty.fileserver.service.UserFileService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.fluharty.fileserver.utils.Constants.*;

@Service
public class UserFileServiceImpl implements UserFileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final UserFilesRepository userFilesRepository;
    private final AWSService awsService;

    public UserFileServiceImpl(UserFilesRepository userFilesRepository, AWSService awsService) {
        this.userFilesRepository = userFilesRepository;
        this.awsService = awsService;
    }

    public List<String> getFiles() {
        try {
            return awsService.listFiles(bucketName);
        } catch (AmazonClientException e) {
            throw new FileServerException(LIST_FILES, e.getMessage(), null);
        }
    }

    @Transactional
    public HttpStatus upload(UserFile userFile) {
        userFilesRepository.save(userFile);
        return HttpStatus.CREATED;
    }

    public byte[] download(String filename) {
        try {
            return awsService.downloadFile(bucketName, filename).toByteArray();
        } catch (AmazonClientException | IOException e) {
            throw new FileServerException(FILE_DOWNLOAD, e.getMessage(), null);
        }
    }

    public void delete(String filename) {
        try {
            awsService.deleteFile(bucketName, filename);
        } catch (AmazonClientException e) {
            throw new FileServerException(FILE_DELETION, e.getMessage(), null);
        }
    }
}
