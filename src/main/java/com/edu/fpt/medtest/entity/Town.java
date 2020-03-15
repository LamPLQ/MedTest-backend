package com.edu.fpt.medtest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "Town")
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String TownCode;

    @Column(name = "TownName")
    private String TownName;

//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "DistrictCode")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(foreignKey = @ForeignKey(name = "DistrictCode"), name = "DistrictCode")
    private District district;

    public String getTownCode() {
        return TownCode;
    }

    public void setTownCode(String townCode) {
        TownCode = townCode;
    }


    public String getTownName() {
        return TownName;
    }

    public void setTownName(String townName) {
        TownName = townName;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
}
