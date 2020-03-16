package com.edu.fpt.medtest.model;


        import com.edu.fpt.medtest.entity.Town;

        import java.util.List;

public class DistrictModel {
    private String districtCode;
    private String districtName;
    private List<Town> listTown;

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public List<Town> getListTown() {
        return listTown;
    }

    public void setListTown(List<Town> listTown) {
        this.listTown = listTown;
    }
}
