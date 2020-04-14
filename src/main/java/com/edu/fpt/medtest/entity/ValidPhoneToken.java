package com.edu.fpt.medtest.entity;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "valid_phone_token")
public class ValidPhoneToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SessionID")
    private int sessionID;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Token")
    private String token;

    @Column(name = "CreatedTime")
    private long createdTime;

    @Column(name = "ExpireTime")
    private long expiredTime;

    public ValidPhoneToken() {
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
}
