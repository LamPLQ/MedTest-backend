package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.TestVersion;

public class VersionResponseModel extends TestVersion {
    private String creatorName;

    public VersionResponseModel() {

    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
