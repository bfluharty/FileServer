package com.fluharty.fileserver.service.impl;

import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import com.fluharty.fileserver.service.AWSService;
import com.fluharty.fileserver.service.UserFileService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
