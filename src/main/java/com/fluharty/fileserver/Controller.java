package com.fluharty.fileserver;

import com.fluharty.fileserver.exception.FileServerException;
import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.service.UserFileService;
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

import static com.fluharty.fileserver.utils.Constants.FILE_NOT_FOUND;

@RestController
public class Controller {
    private final UserFileService userFileService;

    public Controller(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> getFiles(@RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok(userFileService.getFiles());
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("filename") String filename, @RequestHeader Map<String, String> headers) {
        ByteArrayResource resource;

        if (!userFileService.getFiles().contains(filename)) {
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
    public ResponseEntity<UserFile> upload(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        UserFile userFile = new UserFile(headers.get("user"), file.getOriginalFilename(), LocalTime.now());
        return new ResponseEntity<>(userFile, userFileService.upload(userFile));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("filename") String filename, @RequestHeader Map<String, String> headers) {
        if (!userFileService.getFiles().contains(filename)) {
            throw new FileServerException(FILE_NOT_FOUND, null, HttpStatus.NOT_FOUND);
        } else {
            userFileService.delete(filename);
        }
        return ResponseEntity.ok().build();
    }
}