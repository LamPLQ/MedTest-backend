package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.TestType;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
import com.edu.fpt.medtest.model.DistrictModel;
import com.edu.fpt.medtest.model.TestTypeList;
import com.edu.fpt.medtest.repository.TestRepository;
import com.edu.fpt.medtest.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test-types")
public class TestTypeController {

    @Autowired
    private TestTypeService testTypeService;

    @Autowired
    private TestRepository testRepository;

    //get all test type
    @GetMapping("/list")
    public ResponseEntity<?> lsTestType() {
        List<TestType> lsTestType = testTypeService.lsTestType();
        if (lsTestType.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No test type available"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsTestType, HttpStatus.OK);
    }

    //get a test type
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTestType(@PathVariable("id") int testType) {
        Optional<TestType> getTestType = testTypeService.findTestTypeByID(testType);
        if (!getTestType.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Test type not available"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getTestType, HttpStatus.OK);
    }

    //create a test type
    @PostMapping("/create")
    public ResponseEntity<?> createTestType(@RequestBody TestType testType) {
        List<TestType> lsTestType = testTypeService.lsTestType();
        for (TestType typeTrack : lsTestType) {
            if (testType.getTestTypeID() == typeTrack.getTestTypeID())
                return new ResponseEntity<>(new ApiResponse(false, "Already have this test type ID"), HttpStatus.BAD_REQUEST);
        }
        testTypeService.saveTestType(testType);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create test type"), HttpStatus.OK);
    }

    //edit a test type
    @PutMapping(value = "/detail/edit/{id}")
    public ResponseEntity<?> editTestType(@RequestBody TestType testType, @PathVariable("id") int id) {
        Optional<TestType> getTestType = Optional.ofNullable(testTypeService.findTestTypeByID(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestType", "testTypeID", id)));
        testType.setTestTypeID(id);
        testTypeService.saveTestType(testType);
        return new ResponseEntity<>(new ApiResponse(true, "Update Test Type successfully"), HttpStatus.OK);
    }

    //get List testType-test by testTypeID
    @GetMapping("/type-test")
    public ResponseEntity<?> listTestTypeTest() {
        List<TestTypeList> lsResult = new ArrayList<>();
        List<TestType> list = testTypeService.lsTestType();
        for (TestType testType : list) {
            List<Test> testList = testRepository.getAllByTestTypeID(testType.getTestTypeID());
            TestTypeList testTypeList = new TestTypeList();
            testTypeList.setTestTypeTestID(testType.getTestTypeID());
            testTypeList.setTestTypeName(testType.getTestTypeName());
            testTypeList.setListTest(testList);
            lsResult.add(testTypeList);
        }
        return new ResponseEntity<>(lsResult, HttpStatus.OK);
    }

}

