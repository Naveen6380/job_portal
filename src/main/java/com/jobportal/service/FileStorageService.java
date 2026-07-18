package com.jobportal.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileStorageService {

    /**
     * Uploads a file to Cloudinary under the given folder.
     * Returns the secure URL of the uploaded file.
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Deletes a file from Cloudinary using its public_id (extracted from the URL when uploading).
     */
    void deleteFile(String publicId);
}