package com.mislbd.fileuploader.transferfile.implementation;

import com.jcraft.jsch.*;
import com.mislbd.fileuploader.model.entities.UploadedFile;
import com.mislbd.fileuploader.service.UploadedFileService;
import com.mislbd.fileuploader.transferfile.FileTransferService;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Vector;

@Service
public class FileTransferServiceImp implements FileTransferService {

    private final Environment environment;
    private final UploadedFileService fileService;
    private String project, remotePath, localPath, remoteInwardPath, remoteOutwardPath, localInwardPath, localInwardPathByDate,
                   localOutwardPath, today, host, user, password, configurationKey, configurationValue, channelType;
    private int port;
    private ChannelSftp channelSftp;
    private Session session;
    private JSch jSch;
    private File localOutwardFile;

    public FileTransferServiceImp(UploadedFileService fileService, Environment environment) {
        this.fileService = fileService;
        this.environment = environment;

        project = this.environment.getProperty("project.name");
        host = this.environment.getProperty("remote.server.host");
        port = Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("remote.server.port")));
        user = this.environment.getProperty("remote.server.user");
        password = this.environment.getProperty("remote.server.password");
        remotePath = this.environment.getProperty("remote.server.path");
        localPath = this.environment.getProperty("local.server.path");
        configurationKey = this.environment.getProperty("jsch.configuration.key");
        configurationValue = this.environment.getProperty("jsch.configuration.value");
        channelType = this.environment.getProperty("channel.type");

        jSch = new JSch();
        remoteInwardPath = remotePath+ project+ "/"+ "Inward/";
        remoteOutwardPath = remotePath+ project+ "/"+ "Outward/";
        localInwardPath = localPath+ project+ "/"+ "Inward/";
        localOutwardPath = localPath+ project+ "/"+ "Outward/";
    }

    @Override
    public void setUpJsch() throws JSchException {
        session = jSch.getSession(user, host, port);
        session.setConfig(configurationKey, configurationValue);
        session.setPassword(password);
        session.connect();
        channelSftp = (ChannelSftp) session.openChannel(channelType);
        channelSftp.connect();
    }

    @Override
    public void getFileFromRemoteServer() throws JSchException, SftpException {
        setUpJsch();
        today = getCurrentDate();
        createDirIfNotExist(localInwardPath, today);
        try {
            channelSftp.cd(remoteOutwardPath);
            Vector list = channelSftp.ls(remoteOutwardPath);
            for(int i = 0; i < list.size(); i++){
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) list.get(i);
                if (!fileService.isUploaded(entry.getFilename())) {
                    uploadFileToLocalDestination(entry);
                }else{
                    LoggerFactory.getLogger("File Uploader").info("Already imported Inward File: " +entry.getFilename());
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            destroyConnection();
        }

    }

    @Override
    public void setFileToRemoteServer() throws JSchException, SftpException {
        setUpJsch();
        today = getCurrentDate();
        createDirIfNotExist(localOutwardPath, today);
        try {
            for(File fileEntry : localOutwardFile.listFiles()){
                if(!fileService.isUploaded(fileEntry.getName())){
                    channelSftp.put(fileEntry.getAbsolutePath(), remoteInwardPath);
                    fileService.save(new UploadedFile(fileEntry.getName()));
                    LoggerFactory.getLogger("Outward File Uploader").info("Successfully uploaded outward file: " +fileEntry.getName());
                }else{
                    LoggerFactory.getLogger("Outward File Uploader").info("Already uploaded outward file: " +fileEntry.getName());
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            destroyConnection();
        }
    }

    @Override
    public void destroyConnection() {
        try{
            if(channelSftp != null){
                channelSftp.disconnect();
            }
            if(session != null){
                session.disconnect();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadFileToLocalDestination(ChannelSftp.LsEntry entry) throws SftpException {
        channelSftp.get(entry.getFilename(), localInwardPathByDate);
        LoggerFactory.getLogger("Inward File Uploader").info("Successfully imported inward file: " +entry.getFilename());
        fileService.save(new UploadedFile(entry.getFilename()));
    }

    private void createDirIfNotExist(String dirPath, String today){
        File file = new File(dirPath+"/"+today);
        if(!file.exists()){
            file.mkdir();
        }
        if(dirPath.equals(localOutwardPath)){
            localOutwardFile = file;
        }
        if(dirPath.equals(localInwardPath)){
            localInwardPathByDate = localInwardPath+"/"+today;
        }
    }

    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
        return sdf.format(Calendar.getInstance().getTime());
    }

}
