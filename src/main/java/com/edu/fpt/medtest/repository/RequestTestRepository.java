package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.RequestTest;
import com.edu.fpt.medtest.entity.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestTestRepository extends CrudRepository<RequestTest, Integer> {
    List<RequestTest> getAllByRequestID(int requestID);
}
