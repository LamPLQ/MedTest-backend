package com.edu.fpt.medtest.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "test")
public class Test implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int testID;

    @Column(name = "Name")
    private String testName;

    @Column(name = "Price")
    private Long price;

    @Column(name = "testTypeID")
    private int testTypeID;

    public Test() {
    }

    /*@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "testsChosen")
    private Set<Request> requestsChosen = new HashSet<>();

    public Set<Request> getRequestsChosen() {
        return requestsChosen;
    }

    public void setRequestsChosen(Set<Request> requestsChosen) {
        this.requestsChosen = requestsChosen;
    }*/
    public int getTestID() {
        return testID;
    }

    public void setTestID(int testID) {
        this.testID = testID;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getTestTypeID() {
        return testTypeID;
    }

    public void setTestTypeID(int testTypeID) {
        this.testTypeID = testTypeID;
    }
}
