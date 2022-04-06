package com.mislbd.fileuploader.model.entities;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Table(name="quartz_job_configuration")
public class QuartzJobInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String jobType;

    public QuartzJobInfo() {
    }

    public QuartzJobInfo(String jobType) {
        this.jobType = jobType;
    }

    public QuartzJobInfo(int id, String jobType) {
        this.id = id;
        this.jobType = jobType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}
