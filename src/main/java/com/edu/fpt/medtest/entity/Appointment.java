package com.edu.fpt.medtest.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "appointment")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @CreatedDate
    @Column(name = "CreatedTime")
    private Date createdTime;

    @Column(name = "MeetingTime")
    private Date meetingTime;

    @Column(name = "Note")
    private String note;

    @Column(name = "Status")
    private String status;

    @Column(name = "CustomerID")
    private int customerID;

    @Column(name = "CoordinatorID")
    private int coordinatorID;

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCoordinatorID() {
        return coordinatorID;
    }

    public void setCoordinatorID(int coordinatorID) {
        this.coordinatorID = coordinatorID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
