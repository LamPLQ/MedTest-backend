package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.District;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DistrictService {
    void saveDistrict(District district);

    Optional<District> findDistrictByCode(String DistrictCode);

    List<District> listDistrict();

    void deleteDistrict(String DistrictCode);
}
