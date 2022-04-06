package com.mislbd.fileuploader.quartz_scheduler.job;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.mislbd.fileuploader.transferfile.FileTransferService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TaskJob implements Job {


    private final FileTransferService fileTransferService;

    public TaskJob(FileTransferService fileTransferService) {
        this.fileTransferService = fileTransferService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap map = jobExecutionContext.getMergedJobDataMap();
        String type = map.getString("type");

        if(type.equals("Inward")){
            try {
                fileTransferService.getFileFromRemoteServer();
            } catch (JSchException | SftpException e) {
                e.printStackTrace();
            }
        }else if(type.equals("Outward")){
            try {
                fileTransferService.setFileToRemoteServer();
            } catch (JSchException | SftpException e) {
                e.printStackTrace();
            }
        }
    }
}
