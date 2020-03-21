package com.edu.fpt.medtest.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Name")
    private String name;

    @Column(name = "DOB")
    private Date dob;

    @Column(name = "Address")
    private String address;

    @Column(name = "Password")
    private String password;

    @Column(name = "Active")
    //boolean type active = 1, non-active = 0
    private int active;

    @Column(name = "Email")
    private String email;

    @Column(name = "Role")
    private String role;

    @Column(name = "Gender")
    //boolean type: female = 0, male = 1;
    private int gender;

    @Column(name = "Image")
    private String image;

    @Column(name = "TownCode")
    private String townCode;

    @Column(name = "DistrictCode")
    private String districtCode;

    public int getId() {
        return id;
    }

    public void setId(int ID) {
        this.id = ID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date DOB) {
        this.dob = DOB;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = "https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png";
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
}
