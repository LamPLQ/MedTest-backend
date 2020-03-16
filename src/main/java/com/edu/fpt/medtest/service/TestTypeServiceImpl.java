package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.TestType;
import com.edu.fpt.medtest.repository.TestTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestTypeServiceImpl implements TestTypeService {

    @Autowired
    TestTypeRepository testTypeRepository;

    @Override
    public List<TestType> lsTestType() {
        List<TestType> lsTesttype =  testTypeRepository.findAll();
        return lsTesttype;
    }

    @Override
    public Optional<TestType> findTestTypeByID(int testTypeID) {
        Optional<TestType> findTestTypeByID = testTypeRepository.findById(testTypeID);
        return findTestTypeByID;
    }

    @Override
    public void saveTestType(TestType testType) {
        testTypeRepository.save(testType);
    }
}
