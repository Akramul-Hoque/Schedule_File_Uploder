package com.mislbd.fileuploader.service.implementation;

import com.mislbd.fileuploader.model.entities.UploadedFile;
import com.mislbd.fileuploader.repository.UploadedFileRepository;
import com.mislbd.fileuploader.service.UploadedFileService;
import org.springframework.stereotype.Service;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    public UploadedFileServiceImpl(UploadedFileRepository uploadedFileRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @Override
    public void save(UploadedFile uploadedFile) {
        uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public boolean isUploaded(String fileName) {
        UploadedFile file = uploadedFileRepository.findByName(fileName);
        return file != null;
    }
}
