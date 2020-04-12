package com.edu.fpt.medtest.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
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
