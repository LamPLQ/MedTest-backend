package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.TestVersion;

import java.util.List;

public class UpgradeVersionModel extends TestVersion {

    List<Test> lsInputTest;

    public List<Test> getLsInputTest() {
        return lsInputTest;
    }

    public void setLsInputTest(List<Test> lsInputTest) {
        this.lsInputTest = lsInputTest;
    }
}
