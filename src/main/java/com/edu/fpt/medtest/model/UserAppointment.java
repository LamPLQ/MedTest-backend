package com.edu.fpt.medtest.model;

import java.util.Date;

public class UserAppointment {
    private String appointment_userName;

    private String appointment_phoneNumber;

    private Date appointment_DOB;

    private Date appointment_meetingTime;

    private int appointment_status;

    private String appointment_note;

    public String getAppointment_note() {
        return appointment_note;
    }

    public void setAppointment_note(String appointment_note) {
        this.appointment_note = appointment_note;
    }

    public String getAppointment_userName() {
        return appointment_userName;
    }

    public void setAppointment_userName(String appointment_userName) {
        this.appointment_userName = appointment_userName;
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

    public int getAppointment_status() {
        return appointment_status;
    }

    public void setAppointment_status(int appointment_status) {
        this.appointment_status = appointment_status;
    }
}
