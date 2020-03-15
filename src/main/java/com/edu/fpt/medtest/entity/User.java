package com.edu.fpt.medtest.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Name")
    private String Name;

    @Column(name = "DOB")
    private Date DOB;

    @Column(name = "Address")
    private String Address;

    @Column(name = "Password")
    private String Password;

    @Column(name = "Active")
    //boolean type active = 1, non-active = 0
    private int Active;

    @Column(name = "Email")
    private String Email;

    @Column(name = "Role")
    private String Role;

    @Column(name = "Gender")
    //boolean type: female = 0, male = 1;
    private int Gender;

    @Column(name = "Image")
    private String Image;

    @Column(name = "TownCode")
    private String TownCode;

    @Column(name = "DistrictCode")
    private String DistrictCode;

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
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = "Customer";
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTownCode() {
        return TownCode;
    }

    public void setTownCode(String townCode) {
        TownCode = townCode;
    }

    public String getDistrictCode() {
        return DistrictCode;
    }

    public void setDistrictCode(String districtCode) {
        DistrictCode = districtCode;
    }
}
