package com.edu.fpt.medtest.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "temp_request_model")
@EntityListeners(AuditingEntityListener.class)
public class RequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tempRequestID")
    private int tempRequestID;

    @Column(name = "tempUserID")
    private int UserID;

    @CreatedDate
    @Column(name = "tempCreatedTime")
    private Date createdTime;

    @Column(name = "tempMeetingTime")
    private Date meetingTime;

    @Column(name = "tempAddress")
    private String address;

    @Column(name = "tempTownCode")
    private String townCode;

    @Column(name = "tempDistrictCode")
    private String districtCode;

    //List<String> selectedTest;


    public RequestModel() {
    }

    public RequestModel(int userID, Date createdTime, Date meetingTime, String address, String townCode, String districtCode) {
        UserID = userID;
        this.createdTime = createdTime;
        this.meetingTime = meetingTime;
        this.address = address;
        this.townCode = townCode;
        this.districtCode = districtCode;
    }

    public int getTempRequestID() {
        return tempRequestID;
    }

    public void setTempRequestID(int tempRequestID) {
        this.tempRequestID = tempRequestID;
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

   /* public List<String> getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(List<String> selectedTest) {
        this.selectedTest = selectedTest;
    }*/
}
