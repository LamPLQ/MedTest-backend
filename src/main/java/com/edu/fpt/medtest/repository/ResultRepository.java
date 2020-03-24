package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Result;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<Result, Integer> {
    List<Result> getAllByRequestID(int requestID);
}
