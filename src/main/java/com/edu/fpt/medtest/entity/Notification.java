package com.edu.fpt.medtest.entity;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener.class)
public class Notification implements Serializable {

    @Id
    @Column(name = "ID")
    private int ID;

    @Column(name = "CreatedTime")
    @CreatedDate
    private Date createdTime;

    @Column(name = "IsRead")
    private int isRead;

    @Column(name = "Type")
    private String type;

    @Column(name = "Message")
    private String message;

    @Column(name = "UserID")
    private int userID;

    @Column(name = "RequestID", nullable = true)
    private int requestID;

    @Column(name = "AppointmentID", nullable = true)
    private int appointmentID;

    public Notification() {
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

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }
}
