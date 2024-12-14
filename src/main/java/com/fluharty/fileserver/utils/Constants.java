package com.fluharty.fileserver.utils;


import java.util.AbstractMap;

public class Constants {
    public static final AbstractMap.SimpleEntry<String, String> FILE_NOT_FOUND = new AbstractMap.SimpleEntry<>("File Not Found", "The file was not found. Please make sure you selected an existing file.");
    public static final AbstractMap.SimpleEntry<String, String> FILE_DELETION = new AbstractMap.SimpleEntry<>("File Deletion", "There was a problem deleting the file. Please try again later.");
}
