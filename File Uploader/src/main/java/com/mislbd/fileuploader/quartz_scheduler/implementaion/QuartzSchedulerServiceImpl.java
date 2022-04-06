package com.mislbd.fileuploader.quartz_scheduler.implementaion;

import com.mislbd.fileuploader.model.RequestJob;
import com.mislbd.fileuploader.model.RequestTrigger;
import com.mislbd.fileuploader.model.entities.QuartzJobInfo;
import com.mislbd.fileuploader.quartz_scheduler.QuartzSchedulerService;
import com.mislbd.fileuploader.quartz_scheduler.job.TaskJob;
import com.mislbd.fileuploader.service.QuartzJobInfoService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Service
public class QuartzSchedulerServiceImpl implements QuartzSchedulerService {

    private final Scheduler scheduler;
    private final QuartzJobInfoService quartzJobInfoService;

    public QuartzSchedulerServiceImpl(Scheduler scheduler, QuartzJobInfoService quartzJobInfoService){
        this.scheduler = scheduler;
        this.quartzJobInfoService = quartzJobInfoService;
    }

    @Override
    public void saveJob(RequestJob requestJob) {
        QuartzJobInfo quartzJobInfoById = quartzJobInfoService.getQuartzJobInfoById(requestJob.getJobInformationId());
        JobDetail job = buildJobDetail(requestJob, quartzJobInfoById);
        try {
            if(scheduler.checkExists(job.getKey())){
                LoggerFactory.getLogger("Scheduler").info("Job Exist with the key: "+job.getKey().getName());
            }else{
                scheduler.addJob(job, true);
                LoggerFactory.getLogger("Scheduler").info("Job Created for the key: "+job.getKey().getName());
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveTrigger(RequestTrigger requestTrigger) {
        SimpleTrigger simpleTrigger = buildJobTrigger(requestTrigger);
        try {
            scheduler.scheduleJob(simpleTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


    private JobDetail buildJobDetail(RequestJob requestJob, QuartzJobInfo quartzJobInfo) {
        JobDataMap map = new JobDataMap();
        map.put("type", quartzJobInfo.getJobType());

        return JobBuilder.newJob(TaskJob.class)
                .withIdentity(requestJob.getJobKey())
                .withDescription(requestJob.getJobDescription())
                .usingJobData(map)
                .storeDurably()
                .build();
    }


    private SimpleTrigger buildJobTrigger(RequestTrigger requestTrigger) {
        TriggerBuilder<Trigger> buildTrigger = TriggerBuilder.newTrigger();
        buildTrigger.forJob(requestTrigger.getJob());
        buildTrigger.withIdentity("trigger key: "+requestTrigger.getJob().getKey());
        buildTrigger.withDescription("trigger description: "+requestTrigger.getJob().getDescription());
        if(requestTrigger.getStartAt() == null){
            buildTrigger.startNow();
        }else{
            buildTrigger.startAt(requestTrigger.getStartAt());
        }
        SimpleScheduleBuilder scheduleBuilder = simpleSchedule();
        if(requestTrigger.getTotalInterval() == 0){
            scheduleBuilder.repeatForever();
        }else{
            scheduleBuilder.withRepeatCount(requestTrigger.getTotalInterval());
        }
        scheduleBuilder.withIntervalInSeconds(requestTrigger.getFrequency());
        buildTrigger.withSchedule(scheduleBuilder);

        return (SimpleTrigger) buildTrigger.build();

    }

    @Override
    public List<JobDetail> getJobs() {
        return null;
    }

    @Override
    public JobDetail getJobByKey(JobKey jobKey) {
        JobDetail jobDetail = null;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobDetail;
    }

    @Override
    public void updateJob(RequestJob requestJob) {
        QuartzJobInfo info = quartzJobInfoService.getQuartzJobInfoById(requestJob.getJobInformationId());
        JobDetail jobDetail = buildJobDetail(requestJob, info);
        try {

            if(!scheduler.checkExists(jobDetail.getKey())){
                LoggerFactory.getLogger("Scheduler").info("No job found by this key: "+jobDetail.getKey().getName());
                return;
            }
            scheduler.addJob(jobDetail, true);
            LoggerFactory.getLogger("Scheduler").info("Job Updated for key: "+jobDetail.getKey().getName());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelTrigger(JobKey jobKey) {
        try {
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            for(Trigger trigger : triggersOfJob){
                scheduler.unscheduleJob(trigger.getKey());
                LoggerFactory.getLogger("Scheduler").info("Cancel Running Job");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseJob(JobKey key) {
        try {
            scheduler.pauseJob(key);
            LoggerFactory.getLogger("Scheduler").info("Pause Running Job for key: "+key.getName());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resumeJob(JobKey key) {
        try {
            boolean isEmpty = true;
            JobDetail job = scheduler.getJobDetail(key);
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
            for(Trigger t : triggersOfJob){
                if(scheduler.getTriggerState(t.getKey()) == Trigger.TriggerState.PAUSED){
                    scheduler.resumeJob(job.getKey());
                    LoggerFactory.getLogger("Scheduler").info("Job paused for key: "+job.getKey().getName());
                    isEmpty = false;
                }
            }
            if(isEmpty){
                LoggerFactory.getLogger("Scheduler").info("No paused job for key: "+job.getKey().getName());
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRunningJob() {

    }
}
