package com.fluharty.fileserver;

import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.service.UserFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<String>> getFiles() {
        return new ResponseEntity<>(userFileService.getFiles(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<UserFile> uploadFiles(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        UserFile userFile = new UserFile(headers.get("user"), file.getOriginalFilename(), LocalTime.now());
       return new ResponseEntity<>(userFile, userFileService.upload(userFile));
    }
}
