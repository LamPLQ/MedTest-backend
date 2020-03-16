package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.Test;

import java.util.List;

public class TestTypeList {
    private int testTypeTestID;
    private String testTypeName;
    private List<Test> listTest;

    public int getTestTypeTestID() {
        return testTypeTestID;
    }

    public void setTestTypeTestID(int testTypeTestID) {
        this.testTypeTestID = testTypeTestID;
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
