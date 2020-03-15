package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRespository extends JpaRepository<Town, String> {

}
