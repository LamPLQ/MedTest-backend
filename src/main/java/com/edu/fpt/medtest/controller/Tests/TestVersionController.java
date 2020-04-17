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
        List<TestVersion> lsVersions = testVersionService.lsTestVersionByCreatedTimeDesc();
        if (lsVersions.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Hiện tại không có version nào!"), HttpStatus.OK);
        }
        List<VersionResponseModel> lsResponseVersion = new ArrayList<>();
        for (TestVersion testVersion:lsVersions){
            VersionResponseModel versionResponseModel = new VersionResponseModel();
            versionResponseModel.setVersionID(testVersion.getVersionID());
            versionResponseModel.setCreatedTime(testVersion.getCreatedTime());
            versionResponseModel.setCreatorID(testVersion.getCreatorID());
            versionResponseModel.setCreatorName(userRepository.findById(testVersion.getCreatorID()).get().getName());
            lsResponseVersion.add(versionResponseModel);
        }
        return new ResponseEntity<>(lsResponseVersion, HttpStatus.OK);
    }

    //create new version
    @PostMapping("/upgrade-version")
    public ResponseEntity<?> upGradeVersion(@RequestBody UpgradeVersionModel upgradeVersionModel) {
        Optional<User> accessCoor = userRepository.getUserByIdAndRole(upgradeVersionModel.getCreatorID(), "COORDINATOR");
        if (!accessCoor.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Người dùng không có quyền đăng nhập vào tính năng này"), HttpStatus.OK);
        }
        //create a new version
        TestVersion newVersion = new TestVersion();
        newVersion.setCreatorID(upgradeVersionModel.getCreatorID());
        testVersionService.saveATestVersion(newVersion);

        List<TestVersion> lsTestVersion = testVersionService.lsTestVersionByCreatedTimeDesc();
        int currentVersion = lsTestVersion.get(0).getVersionID();
        //System.out.println(lsTestVersion.get(0).getVersionID());
        if(lsTestVersion.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true,"Hệ thống không có xét nghiệm nào!"),HttpStatus.OK);
        }

        //Add list tests input
        List<Test> lsInputTest = upgradeVersionModel.getLsInputTest();
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
        // set current version
        testsOfVersion.setVersionID(currentVersion);

        //set creatorID
        testsOfVersion.setCreatorID(lsTestVersion.get(0).getCreatorID());

        //set createdName
        testsOfVersion.setCreatorName(userRepository.findById(lsTestVersion.get(0).getCreatorID()).get().getName());

        //set createdTime
        testsOfVersion.setCreatedTime(lsTestVersion.get(0).getCreatedTime());

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
            List<Test> testsByTestType = testRepository.getAllByVersionIDAndTestTypeID(currentVersion, testType.getTestTypeID());
            model.setListTest(testsByTestType);
            apiResponse.add(model);
        }
        //set list test
        testsOfVersion.setLsTests(apiResponse);

        return new ResponseEntity<>(testsOfVersion, HttpStatus.OK);
    }

    //list all test of 1 version
    @GetMapping("/list-all-test/{id}")
    public ResponseEntity<?> allTestOfVersion(@PathVariable("id") int versionID) {
        List<Test> testsByVersion = testRepository.getAllByVersionID(versionID);
        if (testsByVersion.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào tại version này!"), HttpStatus.OK);
        }
        Optional<TestVersion> currentTestVersion = testVersionService.testVersionByID(versionID);
        //return
        TestOfVersionModel returnAllTestOfThisVersion = new TestOfVersionModel();
        // set current version
        returnAllTestOfThisVersion.setVersionID(currentTestVersion.get().getVersionID());

        //set creatorID
        returnAllTestOfThisVersion.setCreatorID(currentTestVersion.get().getCreatorID());

        //set createdName
        returnAllTestOfThisVersion.setCreatorName(userRepository.findById(currentTestVersion.get().getCreatorID()).get().getName());

        //set createdTime
        returnAllTestOfThisVersion.setCreatedTime(currentTestVersion.get().getCreatedTime());

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
        return new ResponseEntity<>(returnAllTestOfThisVersion, HttpStatus.OK);
    }


    //list all test of most recently version
    @GetMapping("/lastest-version-test")
    public ResponseEntity<?> lsTestLatestVersion() {
        List<TestVersion> lsTestVersion = testVersionService.lsTestVersionByCreatedTimeDesc();
        if(lsTestVersion.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true,"Hệ thống không có xét nghiệm nào!"),HttpStatus.OK);
        }
        int versionID = lsTestVersion.get(0).getVersionID();
        List<Test> testsByVersion = testRepository.getAllByVersionID(versionID);
        if (testsByVersion.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có xét nghiệm nào tại version này!"), HttpStatus.OK);
        }
        Optional<TestVersion> currentTestVersion = testVersionService.testVersionByID(versionID);
        //return
        TestOfVersionModel returnAllTestOfThisVersion = new TestOfVersionModel();
        // set current version
        returnAllTestOfThisVersion.setVersionID(currentTestVersion.get().getVersionID());

        //set creatorID
        returnAllTestOfThisVersion.setCreatorID(currentTestVersion.get().getCreatorID());

        //set createdName
        returnAllTestOfThisVersion.setCreatorName(userRepository.findById(currentTestVersion.get().getCreatorID()).get().getName());

        //set createdTime
        returnAllTestOfThisVersion.setCreatedTime(currentTestVersion.get().getCreatedTime());

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
        return new ResponseEntity<>(returnAllTestOfThisVersion, HttpStatus.OK);
    }

}