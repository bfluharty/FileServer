package com.fluharty.fileserver.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fluharty.fileserver.service.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AWSServiceImpl implements AWSService {
    @Autowired
    private AmazonS3 s3Client;

    @Override
    public void uploadFile(final String bucketName, final String keyName, final Long contentLength,
                           final String contentType, final InputStream value) throws AmazonClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        s3Client.putObject(bucketName, keyName, value, metadata);
    }

    @Override
    public ByteArrayOutputStream downloadFile(String bucketName, final String keyName) throws IOException,
            AmazonClientException {
        try (S3Object s3Object = s3Client.getObject(bucketName, keyName);
             InputStream inputStream = s3Object.getObjectContent();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream;
        }
    }

    @Override
    public List<String> listFiles(final String bucketName) throws AmazonClientException {
        List<String> keys = new ArrayList<>();
        ObjectListing objectListing = s3Client.listObjects(bucketName);

        do {
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            objectSummaries.stream()
                    .map(S3ObjectSummary::getKey)
                    .filter(key -> !key.endsWith("/"))
                    .forEach(keys::add);
            objectListing = s3Client.listNextBatchOfObjects(objectListing);
        } while (objectListing.isTruncated());

        return keys;
    }

    @Override
    public void deleteFile(final String bucketName, final String keyName) throws AmazonClientException {
        s3Client.deleteObject(bucketName, keyName);
    }
}