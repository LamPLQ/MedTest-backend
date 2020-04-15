package com.edu.fpt.medtest.service.Tests;

import com.edu.fpt.medtest.entity.TestVersion;
import com.edu.fpt.medtest.repository.TestVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestVersionServiceImpl implements TestVersionService {

    @Autowired
    private TestVersionRepository testVersionRepository;

    @Override
    public List<TestVersion> listAllVersion() {
        List<TestVersion> lsVersions = testVersionRepository.findAll();
        return lsVersions;
    }

    @Override
    public Optional<TestVersion> testVersionByID(int testVersionID) {
        Optional<TestVersion> testVersion = testVersionRepository.findById(testVersionID);
        return testVersion;
    }

    @Override
    public void saveATestVersion(TestVersion testVersion) {
        testVersionRepository.save(testVersion);
    }

    @Override
    public List<TestVersion> lsTestVersionByCreatedTimeDesc() {
        List<TestVersion> lsTestVersionByCreatedTimeDesc = testVersionRepository.getAllByOrderByCreatedTimeDesc();
        return lsTestVersionByCreatedTimeDesc;
    }
}
