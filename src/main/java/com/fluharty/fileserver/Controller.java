package com.fluharty.fileserver;

import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.Map;

@RestController
public class Controller {
    private final UserFilesRepository userFilesRepository;

    public Controller(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    @GetMapping("/")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/files")
    public ResponseEntity<Iterable<UserFile>> getFiles() {
        return new ResponseEntity<>(this.userFilesRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<UserFile> uploadFiles(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        UserFile userFile = new UserFile(headers.get("user"), file.getOriginalFilename(), LocalTime.now());
        return new ResponseEntity<>(userFile, HttpStatus.CREATED);
    }
}
