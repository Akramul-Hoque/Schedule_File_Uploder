package com.mislbd.fileuploader.service;

import com.mislbd.fileuploader.model.entities.QuartzJobInfo;

import java.util.List;

public interface QuartzJobInfoService {
    void saveQuartzJob(QuartzJobInfo quartzJobInfo);
    QuartzJobInfo getQuartzJobInfoById(int id);
    List<QuartzJobInfo> getAllQuartzJobInfo();
}
