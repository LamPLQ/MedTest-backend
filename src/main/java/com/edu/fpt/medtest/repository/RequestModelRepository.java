package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.model.RequestModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestModelRepository extends JpaRepository<RequestModel,Integer> {
}
