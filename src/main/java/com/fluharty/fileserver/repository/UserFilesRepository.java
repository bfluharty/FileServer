package com.fluharty.fileserver.repository;

import com.fluharty.fileserver.model.UserFile;
import org.springframework.data.repository.CrudRepository;

public interface UserFilesRepository extends CrudRepository<UserFile, Integer> {}
