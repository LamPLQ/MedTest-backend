package com.edu.fpt.medtest.model;

import java.util.List;

public class CustomerModel {
    private int ID;

    private List<UserAppointment> userAppointments;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public List<UserAppointment> getUserAppointments() {
        return userAppointments;
    }

    public void setUserAppointments(List<UserAppointment> userAppointments) {
        this.userAppointments = userAppointments;
    }
}
