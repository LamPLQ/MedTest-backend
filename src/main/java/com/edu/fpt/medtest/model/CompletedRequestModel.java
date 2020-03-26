package com.edu.fpt.medtest.model;

import java.util.Date;

public class CompletedRequestModel extends DetailRequestModel {
    private Date requestAcceptedTime;

    private Date requestTransportingTime;

    public Date getRequestAcceptedTime() {
        return requestAcceptedTime;
    }

    public void setRequestAcceptedTime(Date requestAcceptedTime) {
        this.requestAcceptedTime = requestAcceptedTime;
    }

    public Date getRequestTransportingTime() {
        return requestTransportingTime;
    }

    public void setRequestTransportingTime(Date requestTransportingTime) {
        this.requestTransportingTime = requestTransportingTime;
    }
}
