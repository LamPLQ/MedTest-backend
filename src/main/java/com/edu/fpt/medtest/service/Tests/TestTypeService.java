package com.edu.fpt.medtest.service.Tests;

import com.edu.fpt.medtest.entity.TestType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TestTypeService  {
    List<TestType> lsTestType();
    Optional<TestType> findTestTypeByID(int testTypeID);
    void saveTestType(TestType testType);
}
