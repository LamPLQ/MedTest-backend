package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TownRepository extends JpaRepository<Town, String> {
     List<Town> getAllByDistrictCode(String districtCode);
}
