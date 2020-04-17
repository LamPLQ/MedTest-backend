package com.edu.fpt.medtest.model;

import java.util.Date;
import java.util.List;

public class DetailRequestModel {
    private String requestID;
    private String customerID;
    private String customerName;
    private String customerPhoneNumber;
    private Date customerDOB;
    private String requestAddress;
    private String requestDistrictID;
    private String requestDistrictName;
    private String requestTownID;
    private String requestTownName;
    private Date requestMeetingTime;
    private String requestStatus;
    private String requestCreatedTime;
    private String nurseID;
    private String nurseName;
    private String coordinatorID;
    private String coordinatorName;
    private String requestAmount;
    private String requestNote;
    private List<String> lsSelectedTest;

    public String getRequestDistrictID() {
        return requestDistrictID;
    }

    public void setRequestDistrictID(String requestDistrictID) {
        this.requestDistrictID = requestDistrictID;
    }

    public String getRequestDistrictName() {
        return requestDistrictName;
    }

    public void setRequestDistrictName(String requestDistrictName) {
        this.requestDistrictName = requestDistrictName;
    }

    public String getRequestTownID() {
        return requestTownID;
    }

    public void setRequestTownID(String requestTownID) {
        this.requestTownID = requestTownID;
    }

    public String getRequestTownName() {
        return requestTownName;
    }

    public void setRequestTownName(String requestTownName) {
        this.requestTownName = requestTownName;
    }

    public String getRequestNote() {
        return requestNote;
    }

    public void setRequestNote(String requestNote) {
        this.requestNote = requestNote;
    }

    public List<String> getLsSelectedTest() {
        return lsSelectedTest;
    }

    public void setLsSelectedTest(List<String> lsSelectedTest) {
        this.lsSelectedTest = lsSelectedTest;
    }

    public String getRequestCreatedTime() {
        return requestCreatedTime;
    }

    public void setRequestCreatedTime(String requestCreatedTime) {
        this.requestCreatedTime = requestCreatedTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Date getCustomerDOB() {
        return customerDOB;
    }

    public void setCustomerDOB(Date customerDOB) {
        this.customerDOB = customerDOB;
    }

    public String getRequestAddress() {
        return requestAddress;
    }

    public void setRequestAddress(String requestAddress) {
        this.requestAddress = requestAddress;
    }

    public Date getRequestMeetingTime() {
        return requestMeetingTime;
    }

    public void setRequestMeetingTime(Date requestMeetingTime) {
        this.requestMeetingTime = requestMeetingTime;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getNurseID() {
        return nurseID;
    }

    public void setNurseID(String nurseID) {
        this.nurseID = nurseID;
    }

    public String getCoordinatorID() {
        return coordinatorID;
    }

    public void setCoordinatorID(String coordinatorID) {
        this.coordinatorID = coordinatorID;
    }

    public String getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }
}
