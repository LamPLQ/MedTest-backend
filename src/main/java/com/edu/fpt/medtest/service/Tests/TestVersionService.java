package com.edu.fpt.medtest.service.Tests;

import com.edu.fpt.medtest.entity.TestVersion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TestVersionService {
    List<TestVersion> listAllVersion();

    Optional<TestVersion> testVersionByID(int testVersionID);

    void saveATestVersion(TestVersion testVersion);

    List<TestVersion> lsTestVersionByCreatedTimeDesc();

}
