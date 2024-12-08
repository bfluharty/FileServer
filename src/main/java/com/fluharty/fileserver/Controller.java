package com.fluharty.fileserver;

import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.service.UserFilesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Map;

@RestController
public class Controller {
    private final UserFilesService userFilesService;

    public Controller(UserFilesService userFilesService) {
        this.userFilesService = userFilesService;
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files")
    public ResponseEntity<Iterable<UserFile>> getFiles() {
        return new ResponseEntity<>(userFilesService.getFiles(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<UserFile> uploadFiles(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        UserFile userFile = new UserFile(headers.get("user"), file.getOriginalFilename(), LocalTime.now());
       return new ResponseEntity<>(userFile, userFilesService.upload(userFile));
    }
}
