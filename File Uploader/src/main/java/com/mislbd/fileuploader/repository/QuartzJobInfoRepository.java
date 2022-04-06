package com.mislbd.fileuploader.repository;

import com.mislbd.fileuploader.model.entities.QuartzJobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuartzJobInfoRepository extends JpaRepository<QuartzJobInfo, Integer> {
}
