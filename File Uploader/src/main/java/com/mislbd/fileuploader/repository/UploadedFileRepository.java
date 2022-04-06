package com.mislbd.fileuploader.repository;

import com.mislbd.fileuploader.model.entities.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

    @Query("select u from UploadedFile u where u.fileName = ?1")
    UploadedFile findByName(String fileName);
}
