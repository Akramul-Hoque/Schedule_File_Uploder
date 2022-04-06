package com.mislbd.fileuploader.service;

import com.mislbd.fileuploader.model.entities.UploadedFile;

import java.util.List;

public interface UploadedFileService {
    void save(UploadedFile uploadedFile);
    boolean isUploaded(String fileName);
}
