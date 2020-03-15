package com.edu.fpt.medtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "district")
public class District {

    //    @Id @GeneratedValue(generator="system-uuid")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Id
    @Column(columnDefinition = "text")
    private String DistrictCode;

    @Column(name = "DistrictName")
    private String DistrictName;

    @OneToMany(mappedBy = "district", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Town> towns;

    public District() {
    }

//    public List<Town> getTowns() {
//        return towns;

//    }

//    public void setTowns(List<Town> towns) {
//        this.towns = towns;
//    }

    public String getDistrictCode() {
        return DistrictCode;
    }

    public void setDistrictCode(String districtCode) {
        DistrictCode = districtCode;
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }
}
