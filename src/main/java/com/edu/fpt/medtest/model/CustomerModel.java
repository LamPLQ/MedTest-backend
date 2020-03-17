package com.edu.fpt.medtest.model;

import java.util.Date;
import java.util.List;

public class CustomerModel {
    private int appointmentCustID;

    private String appointmentCustName;

    private String appointmentCustPhoneNumber;

    private  Date appointmentCustDOB;

    public int getAppointmentCustID() {
        return appointmentCustID;
    }

    public void setAppointmentCustID(int appointmentCustID) {
        this.appointmentCustID = appointmentCustID;
    }

    public String getAppointmentCustName() {
        return appointmentCustName;
    }

    public void setAppointmentCustName(String appointmentCustName) {
        this.appointmentCustName = appointmentCustName;
    }

    public String getAppointmentCustPhoneNumber() {
        return appointmentCustPhoneNumber;
    }

    public void setAppointmentCustPhoneNumber(String appointmentCustPhoneNumber) {
        this.appointmentCustPhoneNumber = appointmentCustPhoneNumber;
    }

    public Date getAppointmentCustDOB() {
        return appointmentCustDOB;
    }

    public void setAppointmentCustDOB(Date appointmentCustDOB) {
        this.appointmentCustDOB = appointmentCustDOB;
    }
}
