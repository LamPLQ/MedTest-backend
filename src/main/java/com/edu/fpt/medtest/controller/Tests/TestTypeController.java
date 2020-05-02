package com.edu.fpt.medtest.controller.Tests;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.TestType;
import com.edu.fpt.medtest.model.TestTypeListModel;
import com.edu.fpt.medtest.repository.TestRepository;
import com.edu.fpt.medtest.service.Tests.TestTypeService;
import com.edu.fpt.medtest.utils.ApiResponse;
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
        try {
            List<TestType> lsTestType = testTypeService.lsTestType();
            if (lsTestType.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có loại xét nghiệm nào hiện tại!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsTestType, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get a test type
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTestType(@PathVariable("id") int testType) {
        try {
            Optional<TestType> getTestType = testTypeService.findTestTypeByID(testType);
            if (!getTestType.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Mã loại xét nghiệm không tồn tại!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getTestType, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create a test type
    @PostMapping("/create")
    public ResponseEntity<?> createTestType(@RequestBody TestType testType) {
        try {
            List<TestType> lsTestType = testTypeService.lsTestType();
            for (TestType typeTrack : lsTestType) {
                if (testType.getTestTypeID() == typeTrack.getTestTypeID())
                    return new ResponseEntity<>(new ApiResponse(false, "Đã tồn tại mã loại xét nghiệm!"), HttpStatus.OK);
            }
            testTypeService.saveTestType(testType);
            return new ResponseEntity<>(new ApiResponse(true, "Tạo mã loại xét nghiệm thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //edit a test type
    @PutMapping(value = "/detail/edit/{id}")
    public ResponseEntity<?> editTestType(@RequestBody TestType testType, @PathVariable("id") int id) {
        try {
            Optional<TestType> getTestType = testTypeService.findTestTypeByID(id);
            if (!getTestType.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại mã loại xét nghiệm"), HttpStatus.OK);
            }
            testType.setTestTypeID(id);
            testTypeService.saveTestType(testType);
            return new ResponseEntity<>(getTestType, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get List testType-test by testTypeID
    @GetMapping("/type-test")
    public ResponseEntity<?> listTestTypeTest() {
        try {
            List<TestTypeListModel> lsResult = new ArrayList<>();
            List<TestType> list = testTypeService.lsTestType();
            for (TestType testType : list) {
                List<Test> testList = testRepository.getAllByTestTypeID(testType.getTestTypeID());
                TestTypeListModel testTypeListModel = new TestTypeListModel();
                testTypeListModel.setTestTypeID(testType.getTestTypeID());
                testTypeListModel.setTestTypeName(testType.getTestTypeName());
                testTypeListModel.setListTest(testList);
                lsResult.add(testTypeListModel);
            }
            if(lsResult.isEmpty()){
                return new ResponseEntity<>(new ApiResponse(true,"Danh sách không có kết quả nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsResult, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

}

