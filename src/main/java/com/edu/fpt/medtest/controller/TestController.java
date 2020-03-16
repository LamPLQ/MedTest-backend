package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.TestType;
import com.edu.fpt.medtest.exception.ResourceNotFoundException;
import com.edu.fpt.medtest.service.TestService;
import com.edu.fpt.medtest.service.TestTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/test-types/tests")
public class TestController {

    @Autowired
    private TestService testService;

    //get all test
    @GetMapping("/list")
    public ResponseEntity<?> lsTest() {
        List<Test> lsTest = testService.lsTest();
        if (lsTest.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No test available"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsTest, HttpStatus.OK);
    }

    //get a test
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTest(@PathVariable("id") int id) {
        Optional<Test> getTest = testService.findTestByID(id);
        if (!getTest.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Test not available"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getTest, HttpStatus.OK);
    }

    //create a test
    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody Test test) {
        List<Test> lsTest = testService.lsTest();
        for (Test testTrack : lsTest) {
            if (test.getTestID() == testTrack.getTestID())
                return new ResponseEntity<>(new ApiResponse(false, "Already have this test ID"), HttpStatus.BAD_REQUEST);
        }
        testService.saveTest(test);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create test"), HttpStatus.OK);
    }

    //edit a test type
    @PutMapping(value = "/detail/edit/{id}")
    public ResponseEntity<?> editTest(@RequestBody Test test, @PathVariable("id") int id) {
        Optional<Test> getTest = Optional.ofNullable(testService.findTestByID(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test", "testID", id)));
        test.setTestID(id);
        testService.saveTest(test);
        return new ResponseEntity<>(new ApiResponse(true, "Update Test successfully"), HttpStatus.OK);
    }
}
