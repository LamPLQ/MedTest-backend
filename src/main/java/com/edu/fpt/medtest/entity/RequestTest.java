package com.edu.fpt.medtest.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "request_test")
public class RequestTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestTestID;

    @Column(name = "RequestID")
    private int requestID;

    @Column(name = "TestID")
    private int testID;

    public RequestTest() {
    }

    public int getRequestTestID() {
        return requestTestID;
    }

    public void setRequestTestID(int requestTestID) {
        this.requestTestID = requestTestID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }
}
