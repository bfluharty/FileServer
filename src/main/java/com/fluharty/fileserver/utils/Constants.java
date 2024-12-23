package com.fluharty.fileserver.utils;

public class Constants {
    public static final String FILE_NOT_FOUND = "The file was not found. Please make sure you selected an existing file.";
    public static final String FILE_ALREADY_EXISTS = "There is already a file with the same name. Please choose a different name for this file.";
    public static final String STORAGE_LIMIT_EXCEEDED = "Storage capacity has been reached. Please remove unnecessary files and try again.";
    public static final String FILE_DELETION = "There was a problem deleting the file. Please try again later.";
    public static final String FILE_DOWNLOAD = "There was a problem downloading the file. Please try again later.";
    public static final String FILE_UPLOAD = "There was a problem uploading the file. Please try again later.";
    public static final String LIST_FILES = "There was a problem listing the files. Please try again later.";
    public static final String STORAGE_LOOKUP = "There was a problem getting the amount of storage remaining. Please try again later.";
    public static final long STORAGE_LIMIT_BYTES = 5000000000L;
}
