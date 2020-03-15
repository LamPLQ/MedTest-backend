package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TownRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TownServiceImpl implements TownService {

    @Autowired
    private TownRespository townRespository;

    @Autowired
    private DistrictRepository districtRepository;

    //xet xem districtCode cua 1 town da ton tai trong bang District chua
    //not -> tao ra 1 district moi
    @Override
    public Town saveTown(Town town) {
        District district = districtRepository.findById(town.getDistrict().getDistrictCode()).orElse(null);
        if (district == null) {
            System.out.println(district.getDistrictCode());
        }
        district.setDistrictName(town.getDistrict().getDistrictName());
        town.setDistrict(district);
        return townRespository.save(town);
    }

    @Override
    public List<Town> getListTown() {
        List<Town> towns = townRespository.findAll();
        return townRespository.findAll();
    }

    @Override
    public void deleteTown(String TownCode) {
        townRespository.deleteById(TownCode);
    }
}
