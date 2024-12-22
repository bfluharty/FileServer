package com.fluharty.fileserver.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_files")
public class UserFile {
    public UserFile(String userName, String filename, LocalTime uploadTime) {
        this.uuid = UUID.randomUUID();
        this.userName = userName;
        this.filename = filename;
        this.uploadTime = uploadTime;
    }

    @Id
    private UUID uuid;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "file_name")
    private String filename;

    @Column(name = "upload_time")
    private LocalTime uploadTime;
}
