package com.edu.fpt.medtest.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "request_history")
@EntityListeners(AuditingEntityListener.class)
public class RequestHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int requestHistoryID;

    @CreatedDate
    @Column(name = "Time")
    private Date createdTime;


    @Column(name = "RequestID")
    private int requestID;

    @Column(name = "Status")
    private String status;

    @Column(name = "Note")
    private String note;

    @Column(name = "UserID")
    private int userID;

    public RequestHistory() {

    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRequestHistoryID() {
        return requestHistoryID;
    }

    public void setRequestHistoryID(int requestHistoryID) {
        this.requestHistoryID = requestHistoryID;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
