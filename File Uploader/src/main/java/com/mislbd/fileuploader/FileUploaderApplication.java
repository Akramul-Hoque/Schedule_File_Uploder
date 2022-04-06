package com.mislbd.fileuploader;

import com.mislbd.fileuploader.transferfile.FileTransferService;
import com.mislbd.fileuploader.transferfile.implementation.FileTransferServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploaderApplication.class, args);
    }

}
