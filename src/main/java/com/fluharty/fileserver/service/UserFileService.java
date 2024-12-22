package com.fluharty.fileserver.service;

import com.fluharty.fileserver.model.UserFile;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserFileService {
    List<String> getFiles();

    @Transactional
    void upload(UserFile userFile, MultipartFile file);

    byte[] download(String filename);

    void delete(String filename);
}
