package com.fluharty.fileserver;

import com.fluharty.fileserver.exception.FileServerException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.service.UserFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.fluharty.fileserver.utils.Constants.*;

@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private final UserFileService userFileService;

    public Controller(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        log.trace("Health check successful");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getFiles(@RequestHeader Map<String, String> headers) {
        log.info("Retrieving files for user: {}", headers.get("user"));
        return ResponseEntity.ok(userFileService.getFiles());
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("filename") String filename, @RequestHeader Map<String, String> headers) {
        log.info("Downloading file: {} for user: {}", filename, headers.get("user"));
        ByteArrayResource resource;

        if (!userFileService.getFiles().contains(filename)) {
            log.warn("File not found: {}", filename);
            throw new FileServerException(FILE_NOT_FOUND, null, HttpStatus.NOT_FOUND);
        } else {
            resource = new ByteArrayResource(userFileService.download(filename));
        }

        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        log.info("Uploading file: {} for user: {}", file.getOriginalFilename(), headers.get("user"));

        if (userFileService.getFiles().contains(file.getOriginalFilename())) {
            log.warn("File already exists: {}", file.getOriginalFilename());
            throw new FileServerException(FILE_ALREADY_EXISTS, null, HttpStatus.CONFLICT);
        }

        if ((userFileService.getBucketSize() + file.getSize()) > STORAGE_LIMIT_BYTES) {
            log.warn("Upload failed due to exceeding storage limit");
            throw new FileServerException(STORAGE_LIMIT_EXCEEDED, null, HttpStatus.INSUFFICIENT_STORAGE);
        }

        userFileService.upload(new UserFile(headers.get("user"), file.getOriginalFilename(), LocalTime.now()), file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("filename") String filename, @RequestHeader Map<String, String> headers) {
        log.info("Deleting file: {} for user: {}", filename, headers.get("user"));
        if (!userFileService.getFiles().contains(filename)) {
            log.warn("File not found: {}", filename);
            throw new FileServerException(FILE_NOT_FOUND, null, HttpStatus.NOT_FOUND);
        } else {
            userFileService.delete(filename);
        }
        return ResponseEntity.ok().build();
    }
}