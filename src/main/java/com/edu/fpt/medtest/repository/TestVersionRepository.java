package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.TestVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestVersionRepository extends JpaRepository<TestVersion, Integer> {
    List<TestVersion> getAllByOrderByCreatedTimeDesc();
}
