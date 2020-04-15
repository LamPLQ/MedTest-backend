package com.edu.fpt.medtest.model;

import java.util.List;


public class RequestModelInput extends RequestModel {
    List<String> selectedTest;

    public List<String> getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(List<String> selectedTest) {
        this.selectedTest = selectedTest;
    }
}
