package com.fluharty.fileserver.service;

import com.fluharty.fileserver.model.UserFile;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface UserFileService {
    List<String> getFiles();

    @Transactional
    HttpStatus upload(UserFile userFile);

    byte[] download(String filename);

    void delete(String filename);
}
