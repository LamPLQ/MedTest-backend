package com.edu.fpt.medtest.controller.Tests;

import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.TestType;
import com.edu.fpt.medtest.entity.TestVersion;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.TestOfVersionModel;
import com.edu.fpt.medtest.model.TestTypeListModel;
import com.edu.fpt.medtest.model.UpgradeVersionModel;
import com.edu.fpt.medtest.model.VersionResponseModel;
import com.edu.fpt.medtest.repository.TestRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.Tests.TestService;
import com.edu.fpt.medtest.service.Tests.TestTypeService;
import com.edu.fpt.medtest.service.Tests.TestVersionService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tests/versions")
public class TestVersionController {

    @Autowired
    TestVersionService testVersionService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestService testService;

    @Autowired
    private TestTypeService testTypeService;

    @Autowired
    private TestRepository testRepository;

    //list all version
    @GetMapping("/list")
    public ResponseEntity<?> lsVersions() {
        try {
            List<TestVersion> lsVersions = testVersionService.lsTestVersionByCreatedTimeDesc();
            if (lsVersions.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Hiện tại không có phiên bản nào!"), HttpStatus.OK);
            }
            List<VersionResponseModel> lsResponseVersion = new ArrayList<>();
            for (TestVersion testVersion : lsVersions) {
                VersionResponseModel versionResponseModel = new VersionResponseModel();
                versionResponseModel.setVersionID(testVersion.getVersionID());
                versionResponseModel.setCreatedTime(testVersion.getCreatedTime());
                versionResponseModel.setCreatorID(testVersion.getCreatorID());
                versionResponseModel.setCreatorName(userRepository.findById(testVersion.getCreatorID()).get().getName());
                lsResponseVersion.add(versionResponseModel);
            }
            return new ResponseEntity<>(lsResponseVersion, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create new version
    @PostMapping("/upgrade-version")
    public ResponseEntity<?> upGradeVersion(@RequestBody UpgradeVersionModel upgradeVersionModel) {
        try {
            try {
                if (upgradeVersionModel.getCreatorID() == 0) {
                    return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại"), HttpStatus.OK);
            }
            Optional<User> accessCoor = userRepository.getUserByIdAndRole(upgradeVersionModel.getCreatorID(), "COORDINATOR");
            Optional<User> accessAdmin = userRepository.getUserByIdAndRole(upgradeVersionModel.getCreatorID(), "ADMIN");
            if (!accessCoor.isPresent() && !accessAdmin.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không có quyền đăng nhập vào tính năng này"), HttpStatus.OK);
            }
            Optional<User> getUser = userRepository.findById(upgradeVersionModel.getCreatorID());
            if(getUser.get().getActive() == 0){
                return new ResponseEntity<>(new ApiResponse(false,"Người dùng đang bị khoá!"), HttpStatus.OK);
            }
            //check test
            List<Test> lsInputTest = upgradeVersionModel.getLsInputTest();
            try {
            if (lsInputTest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không cập nhật được phiên bản test mới vì danh sách test trống!"), HttpStatus.OK);
            }}catch (Exception ex){
                return new ResponseEntity<>(new ApiResponse(true, "Không cập nhật được phiên bản test mới vì danh sách test trống!"), HttpStatus.OK);
            }

            for (Test checkTest : lsInputTest) {
                try {
                    if (checkTest.getPrice() == null ||
                            checkTest.getTestName().isEmpty() ||
                            checkTest.getTestTypeID() == 0 ||
                            checkTest.getVersionID() == 0) {
                        return new ResponseEntity<>(new ApiResponse(true, "Không cập nhật được phiên bản test mới vì bài test không hợp lệ!"), HttpStatus.OK);
                    }
                    Optional<TestType> getTestType = testTypeService.findTestTypeByID(checkTest.getTestTypeID());
                    if (!getTestType.isPresent()) {
                        return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại loại bài test!"), HttpStatus.OK);
                    }
                } catch (Exception ex) {
                    return new ResponseEntity<>(new ApiResponse(true, "Không tồn tại loại bài test!"), HttpStatus.OK);
                }
            }
            //create a new version
            TestVersion newVersion = new TestVersion();
            newVersion.setCreatorID(upgradeVersionModel.getCreatorID());
            testVersionService.saveATestVersion(newVersion);
            List<TestVersion> lsTestVersion = testVersionService.lsTestVersionByCreatedTimeDesc();
            int currentVersion = lsTestVersion.get(0).getVersionID();
            if (lsTestVersion.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Hệ thống không có xét nghiệm nào!"), HttpStatus.OK);
            }

            //Add list tests input
            List<Test> lsTest = new ArrayList<>();
            for (Test testInput : lsInputTest) {
                Test newTest = new Test();
                newTest.setTestName(testInput.getTestName());
                newTest.setPrice(testInput.getPrice());
                newTest.setTestTypeID(testInput.getTestTypeID());
                newTest.setVersionID(currentVersion);
                testService.saveTest(newTest);
                lsTest.add(newTest);
            }
            //return
            TestOfVersionModel testsOfVersion = new TestOfVersionModel();
            testsOfVersion.setVersionID(currentVersion);// set current version
            testsOfVersion.setCreatorID(lsTestVersion.get(0).getCreatorID());//set creatorID
            testsOfVersion.setCreatorName(userRepository.findById(lsTestVersion.get(0).getCreatorID()).get().getName());//set createdName
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(lsTestVersion.get(0).getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            testsOfVersion.setCreatedTime(createdTime);//set createdTime
            //list test
            List<TestTypeListModel> apiResponse = new ArrayList<>();
            /*****/
            List<TestType> lsTestType = testTypeService.lsTestType();
            for (TestType testType : lsTestType) {
                TestTypeListModel model = new TestTypeListModel();
                //set test type
                model.setTestTypeID(testType.getTestTypeID());
                //set test type name
                model.setTestTypeName(testType.getTestTypeName());
                //set list test
                List<Test> testsByTestType = testRepository.getAllByVersionIDAndTestTypeID(currentVersion, testType.getTestTypeID());
                model.setListTest(testsByTestType);
                apiResponse.add(model);
            }
            //set list test
            testsOfVersion.setLsTests(apiResponse);
            if (testsOfVersion == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Phiên bản mới chưa được tạo!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(testsOfVersion, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list all test of 1 version
    @GetMapping("/list-all-test/{id}")
    public ResponseEntity<?> allTestOfVersion(@PathVariable("id") int versionID) {
        try {
            List<Test> testsByVersion = testRepository.getAllByVersionID(versionID);
            if (testsByVersion.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào tại phiên bản này!"), HttpStatus.OK);
            }
            Optional<TestVersion> currentTestVersion = testVersionService.testVersionByID(versionID);
            //return
            TestOfVersionModel returnAllTestOfThisVersion = new TestOfVersionModel();// set current version
            returnAllTestOfThisVersion.setVersionID(currentTestVersion.get().getVersionID());//set creatorID
            returnAllTestOfThisVersion.setCreatorID(currentTestVersion.get().getCreatorID());//set createdName
            returnAllTestOfThisVersion.setCreatorName(userRepository.findById(currentTestVersion.get().getCreatorID()).get().getName());
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(currentTestVersion.get().getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            returnAllTestOfThisVersion.setCreatedTime(createdTime);//set createdTime
            //list test
            List<TestTypeListModel> apiResponse = new ArrayList<>();
            /*********/
            List<TestType> lsTestType = testTypeService.lsTestType();
            for (TestType testType : lsTestType) {
                TestTypeListModel model = new TestTypeListModel();
                //set test type
                model.setTestTypeID(testType.getTestTypeID());
                //set test type name
                model.setTestTypeName(testType.getTestTypeName());
                //set list test
                List<Test> testsByTestType = testRepository.getAllByVersionIDAndTestTypeID(currentTestVersion.get().getVersionID(), testType.getTestTypeID());
                model.setListTest(testsByTestType);
                apiResponse.add(model);
            }
            //set list test
            returnAllTestOfThisVersion.setLsTests(apiResponse);
            if (returnAllTestOfThisVersion == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào tại phiên bản hiện tại"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnAllTestOfThisVersion, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }


    //list all test of most recently version
    @GetMapping("/lastest-version-test")
    public ResponseEntity<?> lsTestLatestVersion() {
        try {
            List<TestVersion> lsTestVersion = testVersionService.lsTestVersionByCreatedTimeDesc();
            if (lsTestVersion.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Hệ thống không có xét nghiệm nào!"), HttpStatus.OK);
            }
            int versionID = lsTestVersion.get(0).getVersionID();
            List<Test> testsByVersion = testRepository.getAllByVersionID(versionID);
            if (testsByVersion.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào tại version này!"), HttpStatus.OK);
            }
            Optional<TestVersion> currentTestVersion = testVersionService.testVersionByID(versionID);
            //return
            TestOfVersionModel returnAllTestOfThisVersion = new TestOfVersionModel();
            returnAllTestOfThisVersion.setVersionID(currentTestVersion.get().getVersionID());// set current version
            returnAllTestOfThisVersion.setCreatorID(currentTestVersion.get().getCreatorID()); //set creatorID
            returnAllTestOfThisVersion.setCreatorName(userRepository.findById(currentTestVersion.get().getCreatorID()).get().getName()); //set createdName
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(currentTestVersion.get().getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            returnAllTestOfThisVersion.setCreatedTime(createdTime);//set createdTime

            //list test
            List<TestTypeListModel> apiResponse = new ArrayList<>();
            ///
            List<TestType> lsTestType = testTypeService.lsTestType();
            for (TestType testType : lsTestType) {
                TestTypeListModel model = new TestTypeListModel();
                //set test type
                model.setTestTypeID(testType.getTestTypeID());
                //set test type name
                model.setTestTypeName(testType.getTestTypeName());
                //set list test
                List<Test> testsByTestType = testRepository.getAllByVersionIDAndTestTypeID(currentTestVersion.get().getVersionID(), testType.getTestTypeID());
                model.setListTest(testsByTestType);
                apiResponse.add(model);
            }
            //set list test
            returnAllTestOfThisVersion.setLsTests(apiResponse);
            if (returnAllTestOfThisVersion == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có bài test nào tại version mới nhất!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnAllTestOfThisVersion, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
