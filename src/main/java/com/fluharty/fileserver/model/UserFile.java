package com.fluharty.fileserver.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "user_files")
public class UserFile {
    private UserFile() {}

    public UserFile(String userName, String filename, LocalTime uploadTime) {
        this.userName = userName;
        this.filename = filename;
        this.uploadTime = uploadTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "file_name")
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Column(name = "upload_time")
    private LocalTime uploadTime;

    public LocalTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
