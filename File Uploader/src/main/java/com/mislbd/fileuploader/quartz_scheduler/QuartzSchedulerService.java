package com.mislbd.fileuploader.quartz_scheduler;

import com.mislbd.fileuploader.model.RequestJob;
import com.mislbd.fileuploader.model.RequestTrigger;
import com.mislbd.fileuploader.model.entities.QuartzJobInfo;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;

import java.util.List;

public interface QuartzSchedulerService {
    void saveJob(RequestJob requestJob);
    void saveTrigger(RequestTrigger requestTrigger);
    List<JobDetail> getJobs();
    JobDetail getJobByKey(JobKey jobKey);
    void updateJob(RequestJob requestJob);
    void cancelTrigger(JobKey jobKey);
    void pauseJob(JobKey key);
    void resumeJob(JobKey key);
    void getRunningJob();
}
