package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import com.fluharty.fileserver.service.AWSService;
import com.fluharty.fileserver.service.UserFileService;
import com.fluharty.fileserver.utils.ErrorDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.fluharty.fileserver.utils.Constants.FILE_DELETION;

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
        return awsService.listFiles(bucketName);
    }

    @Transactional
    public HttpStatus upload(UserFile userFile) {
        userFilesRepository.save(userFile);
        return HttpStatus.CREATED;
    }

    public byte[] download(String filename) {
        try {
            return awsService.downloadFile(bucketName, filename).toByteArray();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }

    public ErrorDetails delete(String filename) {
        ErrorDetails error = null;
        try {
            awsService.deleteFile(bucketName, filename);
        } catch (AmazonClientException e) {
            error = new ErrorDetails.Builder(FILE_DELETION.getKey(), FILE_DELETION.getValue())
                    .stacktrace(e.getMessage()).build();
        }
        return error;
    }
}
