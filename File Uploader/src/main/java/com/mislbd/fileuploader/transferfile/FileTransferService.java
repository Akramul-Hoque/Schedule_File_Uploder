package com.mislbd.fileuploader.transferfile;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface FileTransferService {
    void setUpJsch() throws JSchException;
    void getFileFromRemoteServer() throws JSchException, SftpException;
    void setFileToRemoteServer() throws JSchException, SftpException;
    void destroyConnection();
}
