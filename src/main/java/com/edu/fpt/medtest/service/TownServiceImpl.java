package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TownRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TownServiceImpl implements TownService {

    @Autowired
    private TownRespository townRespository;

    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public Town saveTown(Town town) {
       return null;
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

    @Override
    public Optional<Town> getTownByCode(String townCode) {
        Optional<Town> getTownByCode = townRespository.findById(townCode);
        return Optional.empty();
    }


}
