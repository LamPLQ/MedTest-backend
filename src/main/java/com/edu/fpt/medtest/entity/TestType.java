package com.edu.fpt.medtest.entity;

import javax.persistence.*;

@Entity
@Table(name = "testtype")
public class TestType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int testTypeID;

    @Column(name = "Name")
    private String testTypeName;

    public TestType() {
    }

    public int getTestTypeID() {
        return testTypeID;
    }

    public void setTestTypeID(int testTypeID) {
        this.testTypeID = testTypeID;
    }

    public String getTestTypeName() {
        return testTypeName;
    }

    public void setTestTypeName(String typeName) {
        this.testTypeName = typeName;
    }
}
