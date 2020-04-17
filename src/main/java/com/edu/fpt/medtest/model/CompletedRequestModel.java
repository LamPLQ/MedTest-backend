package com.edu.fpt.medtest.model;

import java.util.Date;

public class CompletedRequestModel extends DetailRequestModel {
    private String requestAcceptedTime;

    private String requestTransportingTime;

    public String getRequestAcceptedTime() {
        return requestAcceptedTime;
    }

    public void setRequestAcceptedTime(String requestAcceptedTime) {
        this.requestAcceptedTime = requestAcceptedTime;
    }

    public String getRequestTransportingTime() {
        return requestTransportingTime;
    }

    public void setRequestTransportingTime(String requestTransportingTime) {
        this.requestTransportingTime = requestTransportingTime;
    }
}
