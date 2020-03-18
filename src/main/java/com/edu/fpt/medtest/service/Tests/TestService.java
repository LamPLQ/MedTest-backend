package com.edu.fpt.medtest.service.Tests;

import com.edu.fpt.medtest.entity.Test;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TestService {
    List<Test> lsTest();
    Optional<Test> findTestByID(int testID);
    void saveTest(Test test);
}
