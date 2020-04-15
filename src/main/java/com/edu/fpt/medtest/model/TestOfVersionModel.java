package com.edu.fpt.medtest.model;

import java.util.Date;
import java.util.List;

public class TestOfVersionModel {
    private int versionID;
    private int creatorID;
    private String creatorName;
    private Date createdTime;
    List<TestTypeListModel> lsTests;

    public TestOfVersionModel() {
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public List<TestTypeListModel> getLsTests() {
        return lsTests;
    }

    public void setLsTests(List<TestTypeListModel> lsTests) {
        this.lsTests = lsTests;
    }
}
