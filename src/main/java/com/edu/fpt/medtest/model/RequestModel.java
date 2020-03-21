package com.edu.fpt.medtest.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import java.util.Date;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
public class RequestModel {

    private int UserID;

    @CreatedDate
    private Date createdTime;

    private Date meetingTime;

    private String address;

    private String townCode;

    private String districtCode;

    List<String> selectedTest;

    public RequestModel(int userID, Date createdTime, Date meetingTime, String address, String townCode, String districtCode, List<String> selectedTest) {
        UserID = userID;
        this.createdTime = createdTime;
        this.meetingTime = meetingTime;
        this.address = address;
        this.townCode = townCode;
        this.districtCode = districtCode;
        this.selectedTest = selectedTest;
    }

    public RequestModel() {
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public List<String> getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(List<String> selectedTest) {
        this.selectedTest = selectedTest;
    }
}
