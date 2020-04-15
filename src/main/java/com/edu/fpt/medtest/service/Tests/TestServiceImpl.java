package com.edu.fpt.medtest.service.Tests;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Override
    public List<Test> lsTest() {
        List<Test> lsTest = testRepository.findAll();
        return lsTest;
    }

    @Override
    public Optional<Test> findTestByID(int testID) {
        Optional<Test> findTestByID = testRepository.findById(testID);
        return findTestByID;
    }

    @Override
    public void saveTest(Test test) {
        testRepository.save(test);
    }

    @Override
    public List<Test> getTestsByVersion(int versionID) {
        List<Test> lsTestsByVersion = testRepository.getAllByVersionID(versionID);
        return lsTestsByVersion;
    }
}
