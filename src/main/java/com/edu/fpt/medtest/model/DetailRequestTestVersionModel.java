package com.edu.fpt.medtest.model;

import com.edu.fpt.medtest.entity.Test;

import java.util.List;

public class DetailRequestTestVersionModel extends DetailRequestModel {
    private List<TestTypeListModel> detailListTest;

    public List<TestTypeListModel> getDetailListTest() {
        return detailListTest;
    }

    public void setDetailListTest(List<TestTypeListModel> detailListTest) {
        this.detailListTest = detailListTest;
    }
}
