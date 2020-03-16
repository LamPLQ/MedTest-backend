package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
}
