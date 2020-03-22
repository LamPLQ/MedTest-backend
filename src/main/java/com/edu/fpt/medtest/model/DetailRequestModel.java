package com.edu.fpt.medtest.model;

import java.util.Date;
import java.util.List;

public class DetailRequestModel {
    private int requestID;
    private int customerID;
    private String customerName;
    private String customerPhoneNumber;
    private Date customerDOB;
    private String customerAddress;
    private Date requestMeetingTime;
    private String requestStatus;
    private Date requestCreatedTime;
    private int nurseID;
    private String nurseName;
    private int coordinatorID;
    private String coordinatorName;
    private long requestAmount;
    private String requestNote;
    private List<String> lsSelectedTest;

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

    public Date getRequestCreatedTime() {
        return requestCreatedTime;
    }

    public void setRequestCreatedTime(Date requestCreatedTime) {
        this.requestCreatedTime = requestCreatedTime;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
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

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
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

    public int getNurseID() {
        return nurseID;
    }

    public void setNurseID(int nurseID) {
        this.nurseID = nurseID;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public int getCoordinatorID() {
        return coordinatorID;
    }

    public void setCoordinatorID(int coordinatorID) {
        this.coordinatorID = coordinatorID;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public long getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(long requestAmount) {
        this.requestAmount = requestAmount;
    }
}
