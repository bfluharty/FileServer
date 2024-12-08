package com.fluharty.fileserver.service;

import com.fluharty.fileserver.model.UserFile;
import com.fluharty.fileserver.repository.UserFilesRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserFilesService {
    private final UserFilesRepository userFilesRepository;

    public UserFilesService(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    public Iterable<UserFile> getFiles() {
        return userFilesRepository.findAll();
    }

    @Transactional
    public HttpStatus upload(UserFile userFile) {
        userFilesRepository.save(userFile);
        return HttpStatus.CREATED;
    }
}
