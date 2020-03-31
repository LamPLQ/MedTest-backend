package com.edu.fpt.medtest.service.Address;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    private DistrictRepository districtRepository;


    @Override
    public void saveDistrict(District district) {
        districtRepository.save(district);
    }

    @Override
    public Optional<District> findDistrictByCode(String DistrictCode) {
        Optional<District> getDistrictByCode = districtRepository.findById(DistrictCode);
        return getDistrictByCode;
    }

    // list all district
    @Override
    public List<District> listDistrict() {
        List<District> listDistrict = districtRepository.findAll();
        return listDistrict;
    }

    @Override
    public void deleteDistrict(String code) {
        districtRepository.deleteById(code);
    }
}
