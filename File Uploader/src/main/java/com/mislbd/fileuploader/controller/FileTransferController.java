package com.mislbd.fileuploader.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.mislbd.fileuploader.model.RequestJob;
import com.mislbd.fileuploader.model.SchedulerJobDTO;
import com.mislbd.fileuploader.model.entities.QuartzJobInfo;
import com.mislbd.fileuploader.quartz_scheduler.QuartzSchedulerService;
import com.mislbd.fileuploader.service.QuartzJobInfoService;
import com.mislbd.fileuploader.transferfile.FileTransferService;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileTransferController {

    private final QuartzJobInfoService quartzJobInfoService;
    private final QuartzSchedulerService quartzSchedulerService;
    private final FileTransferService fileTransferService;

    public FileTransferController(QuartzJobInfoService quartzJobInfoService, QuartzSchedulerService quartzSchedulerService, FileTransferService fileTransferService) {
        this.quartzJobInfoService = quartzJobInfoService;
        this.quartzSchedulerService = quartzSchedulerService;
        this.fileTransferService = fileTransferService;
    }

    @PostMapping("schedule-file")
    public void schedulingFile(@RequestBody SchedulerJobDTO job){
        JobKey key = new JobKey(job.getJobKey());
        JobDetail jobDetail = quartzSchedulerService.getJobByKey(key);
        job.getRequestTrigger().setJob(jobDetail);
        quartzSchedulerService.saveTrigger(job.getRequestTrigger());
    }

    @PostMapping("save-job-info")
    public void saveJobInfo(@RequestBody QuartzJobInfo jobInfo){
        try{
            quartzJobInfoService.saveQuartzJob(jobInfo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @PostMapping("save-job")
    public void saveJob(@RequestBody RequestJob requestJob){
        quartzSchedulerService.saveJob(requestJob);
    }

    @GetMapping("check")
    public void check(){
        try {
            fileTransferService.getFileFromRemoteServer();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("get-job/{jobKey}")
    public void getJob(@PathVariable String jobKey){
        JobKey key = new JobKey(jobKey);
        JobDetail jobByKey = quartzSchedulerService.getJobByKey(key);
        LoggerFactory.getLogger("logger").info(jobByKey.getKey().toString());
    }

    @GetMapping("cancel-running-job/{jobKey}")
    public void cancelTrigger(@PathVariable String jobKey){
        JobKey key = new JobKey(jobKey);
        quartzSchedulerService.cancelTrigger(key);
    }

    @GetMapping("resume-running-job/{jobKey}")
    public void resumeJob(@PathVariable String jobKey){
        JobKey key = new JobKey(jobKey);
        quartzSchedulerService.resumeJob(key);
    }

    @GetMapping("pause-job/{jobKey}")
    public void pauseJob(@PathVariable String jobKey){
        JobKey key = new JobKey(jobKey);
        quartzSchedulerService.pauseJob(key);
    }

    @PostMapping("update-job")
    public void updateJob(@RequestBody RequestJob requestJob){
        quartzSchedulerService.updateJob(requestJob);
    }

}

