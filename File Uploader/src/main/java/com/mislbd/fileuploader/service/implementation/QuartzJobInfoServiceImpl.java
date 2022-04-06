package com.mislbd.fileuploader.service.implementation;

import com.mislbd.fileuploader.model.entities.QuartzJobInfo;
import com.mislbd.fileuploader.repository.QuartzJobInfoRepository;
import com.mislbd.fileuploader.service.QuartzJobInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartzJobInfoServiceImpl implements QuartzJobInfoService {
    private final QuartzJobInfoRepository quartzJobInfoRepository;

    public QuartzJobInfoServiceImpl(QuartzJobInfoRepository quartzJobInfoRepository){
        this.quartzJobInfoRepository = quartzJobInfoRepository;
    }


    @Override
    public void saveQuartzJob(QuartzJobInfo quartzJobInfo) {
        quartzJobInfoRepository.save(quartzJobInfo);
    }

    @Override
    public QuartzJobInfo getQuartzJobInfoById(int id) {
        return quartzJobInfoRepository.findById(id).get();
    }

    @Override
    public List<QuartzJobInfo> getAllQuartzJobInfo() {
        return quartzJobInfoRepository.findAll();
    }
}
