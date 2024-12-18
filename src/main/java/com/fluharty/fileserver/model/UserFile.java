package com.fluharty.fileserver.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "user_files")
public class UserFile {
    private UserFile() {}

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

    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalTime getUploadTime() {
        return uploadTime;
    }
    public void setUploadTime(LocalTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
