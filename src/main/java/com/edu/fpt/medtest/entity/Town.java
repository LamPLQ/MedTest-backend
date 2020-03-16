package com.edu.fpt.medtest.entity;

import javax.persistence.*;

@Entity
@Table(name = "Town")
public class Town {

    @Id
    @Column(columnDefinition = "text")
    private String townCode;

    @Column(name = "TownName")
    private String townName;

    @Column(name = "DistrictCode")
    private String districtCode;

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }


    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }
}
