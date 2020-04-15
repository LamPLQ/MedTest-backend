package com.edu.fpt.medtest.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "temp_appointment_model")
@EntityListeners(AuditingEntityListener.class)
public class AppointmentModelInput {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tempAppointmentID")
    private int tempAppointmentID;

    @Column(name = "tempMeetingTime")
    private Date temMeetingTime;

    @Column(name = "tempCreatedTime")
    @CreatedDate
    private Date tempCreatedTime;

    @Column(name = "tempCustomerID")
    private int temCustomerID;

    public AppointmentModelInput() {
    }

    public int getTempAppointmentID() {
        return tempAppointmentID;
    }

    public void setTempAppointmentID(int tempAppointmentID) {
        this.tempAppointmentID = tempAppointmentID;
    }

    public Date getTemMeetingTime() {
        return temMeetingTime;
    }

    public void setTemMeetingTime(Date temMeetingTime) {
        this.temMeetingTime = temMeetingTime;
    }

    public Date getTempCreatedTime() {
        return tempCreatedTime;
    }

    public void setTempCreatedTime(Date tempCreatedTime) {
        this.tempCreatedTime = tempCreatedTime;
    }

    public int getTemCustomerID() {
        return temCustomerID;
    }

    public void setTemCustomerID(int temCustomerID) {
        this.temCustomerID = temCustomerID;
    }
}
