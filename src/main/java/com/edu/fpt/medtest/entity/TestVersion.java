package com.edu.fpt.medtest.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "test_version")
@EntityListeners(AuditingEntityListener.class)
public class TestVersion {

    @Id
    @Column(name = "VersionID")
    private int versionID;

    @Column(name = "CreatedTime")
    @CreatedDate
    private Date createdTime;

    @Column(name = "creatorID")
    private int CreatorID;

    public TestVersion() {
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getCreatorID() {
        return CreatorID;
    }

    public void setCreatorID(int creatorID) {
        CreatorID = creatorID;
    }
}
