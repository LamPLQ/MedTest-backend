package com.edu.fpt.medtest.controller.Tests;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.service.Tests.TestService;
import com.edu.fpt.medtest.utils.ApiResponse;
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
        try {
            List<Test> lsTest = testService.lsTest();
            if (lsTest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào hiện tại!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsTest, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get a test
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTest(@PathVariable("id") int id) {
        try {
            Optional<Test> getTest = testService.findTestByID(id);
            if (!getTest.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getTest, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create a test
    @PostMapping("/create")
    public ResponseEntity<?> createTest(@RequestBody Test test) {
        try {
            List<Test> lsTest = testService.lsTest();
            for (Test testTrack : lsTest) {
                if (test.getTestID() == testTrack.getTestID())
                    return new ResponseEntity<>(new ApiResponse(false, "Mã xét nghiệm đã tồn tại!"), HttpStatus.OK);
            }
            testService.saveTest(test);
            return new ResponseEntity<>(new ApiResponse(true, "Tạo thành công một mẫu xét nghiệm mới!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
