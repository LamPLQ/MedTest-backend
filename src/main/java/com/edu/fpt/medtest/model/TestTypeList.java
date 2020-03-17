package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.Test;

import java.util.List;

public class TestTypeList {
    private int testTypeID;
    private String testTypeName;
    private List<Test> listTest;

    public int getTestTypeID() {
        return testTypeID;
    }

    public void setTestTypeID(int testTypeID) {
        this.testTypeID = testTypeID;
    }

    public String getTestTypeName() {
        return testTypeName;
    }

    public void setTestTypeName(String testTypeName) {
        this.testTypeName = testTypeName;
    }

    public List<Test> getListTest() {
        return listTest;
    }

    public void setListTest(List<Test> listTest) {
        this.listTest = listTest;
    }
}
