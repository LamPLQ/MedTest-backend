package com.edu.fpt.medtest.model;

import java.util.Date;

public class UserAppointment {
    private String appointment_customerName;

    private String appointment_phoneNumber;

    private Date appointment_DOB;

    private Date appointment_meetingTime;

    private String appointment_status;

    private String appointment_note;

    public String getAppointment_note() {
        return appointment_note;
    }

    public void setAppointment_note(String appointment_note) {
        this.appointment_note = appointment_note;
    }

    public String getAppointment_customerName() {
        return appointment_customerName;
    }

    public void setAppointment_customerName(String appointment_customerName) {
        this.appointment_customerName = appointment_customerName;
    }

    public String getAppointment_phoneNumber() {
        return appointment_phoneNumber;
    }

    public void setAppointment_phoneNumber(String appointment_phoneNumber) {
        this.appointment_phoneNumber = appointment_phoneNumber;
    }

    public Date getAppointment_DOB() {
        return appointment_DOB;
    }

    public void setAppointment_DOB(Date appointment_DOB) {
        this.appointment_DOB = appointment_DOB;
    }

    public Date getAppointment_meetingTime() {
        return appointment_meetingTime;
    }

    public void setAppointment_meetingTime(Date appointment_meetingTime) {
        this.appointment_meetingTime = appointment_meetingTime;
    }

    public String getAppointment_status() {
        return appointment_status;
    }

    public void setAppointment_status(String appointment_status) {
        this.appointment_status = appointment_status;
    }
}
