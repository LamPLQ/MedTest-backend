package com.edu.fpt.medtest.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "request")
@EntityListeners(AuditingEntityListener.class)
public class Request implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private int requestID;

    @Column(name = "UserID")
    private int userID;

    @CreatedDate
    @Column(name = "CreatedTime")
    private Date createdDate;

    @Column(name = "MeetingTime")
    private Date meetingTime;

    @Column(name = "Address")
    private String address;

    @Column(name = "TownCode")
    private String townCode;

    @Column(name = "DistrictCode")
    private String districtCode;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "request_test",
            joinColumns = {@JoinColumn(name = "RequestID", referencedColumnName = "RequestID", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "TestID", referencedColumnName = "ID", nullable = false, updatable = false)})
    private Set<Test> testsChosen = new HashSet<>();

    public Request() {
    }

    public Set<Test> getTestsChosen() {
        return testsChosen;
    }

    public void setTestsChosen(Set<Test> testsChoosen) {
        this.testsChosen = testsChoosen;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
