package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Request;
import com.edu.fpt.medtest.entity.RequestHistory;
import com.edu.fpt.medtest.entity.Test;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.DetailRequestModel;
import com.edu.fpt.medtest.model.RequestModel;
import com.edu.fpt.medtest.repository.RequestHistoryRepository;
import com.edu.fpt.medtest.repository.RequestRepository;
import com.edu.fpt.medtest.repository.TestRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import com.edu.fpt.medtest.entity.RequestTest;
//import com.edu.fpt.medtest.service.Request.RequestTestService;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestHistoryService requestHistoryService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;
/*
    @Autowired
    private RequestTestService requestTestService;*/


  /*  @PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody RequestModel requestModel) {
        for (Test testChosen : request.getTestsChosen()) {
            testRepository.saveAll(Arrays.asList(testChosen));
            request.getTestsChosen().addAll(Arrays.asList(testChosen));
        }
        System.out.println(request.getTestsChosen());
        requestService.saveRequest(request);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create request"), HttpStatus.OK);

        Request request = new Request();
        request.setUserID(requestModel.getUserID());
        request.setMeetingTime(requestModel.getMeetingTime());
        request.setCreatedDate(requestModel.getCreatedTime());
        request.setAddress(requestModel.getAddress());
        request.setTownCode(requestModel.getTownCode());
        request.setDistrictCode(requestModel.getDistrictCode());
        requestRepository.save(request);

        List<String> selectedTests = requestModel.getSelectedTest();
        int selectedTestID;
        boolean existedTest = false;
        //List<RequestTest> lsRequestTest = new ArrayList<>();
        for (String selectedTest : selectedTests) {
            selectedTestID = Integer.parseInt(selectedTest);
            existedTest = testRepository.existsByTestID(selectedTestID);
            if (existedTest == true) {
                RequestTest requestTest = new RequestTest();
                requestTest.setRequestID(request.getRequestID());
                requestTest.setTestID(selectedTestID);
                lsRequestTest.add(requestTest);

          selectedTests.add(selectedTest);
            }else{
                break;
            }
        }
        System.out.println(selectedTests);
        //requestTestService.saveListRequestTest(lsRequestTest);
        if(existedTest == false) return new ResponseEntity<>(new ApiResponse(true, "Not found this test"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(request,HttpStatus.OK);
    }*/


    @PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody RequestModel requestModel) {
        Request request = new Request();
        request.setUserID(requestModel.getUserID());
        request.setMeetingTime(requestModel.getMeetingTime());
        request.setCreatedDate(requestModel.getCreatedTime());
        request.setAddress(requestModel.getAddress());
        request.setTownCode(requestModel.getTownCode());
        request.setDistrictCode(requestModel.getDistrictCode());
        List<String> selectedTests = requestModel.getSelectedTest();
        int selectedTestID;
        boolean existedTest = false;
        List<Test> lsSelectedTest = new ArrayList<>();
        for (String selectedTest : selectedTests) {
            selectedTestID = Integer.parseInt(selectedTest);
            existedTest = testRepository.existsByTestID(selectedTestID);
            if (existedTest == false) {
                return new ResponseEntity<>(new ApiResponse(true, "Not found this test"), HttpStatus.NOT_FOUND);
            } else {
                Optional<Test> selectedTestObj = testRepository.findById(selectedTestID);
                lsSelectedTest.add(selectedTestObj.get());
            }
        }
        System.out.println(lsSelectedTest);
        /*================infinitely list but still insert successfully to database???====================*/
        try {

            for (Test selectingTest : lsSelectedTest) {
                request.getTestsChosen().add(selectingTest);
                selectingTest.getRequestsChosen().add(request);
                // break;
            }
            //request.getTestsChosen().add(lsSelectedTest.get(1));
            //lsSelectedTest.get(1).getRequestsChosen().add(request);
            requestRepository.save(request);
            //requestService.saveRequest(request);
        } catch (Exception e) {
            System.out.println(e);
        }
        /*=========================================================*/
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    //update status of 1 request - add into request_history table
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRequestStatus(@RequestBody RequestHistory requestHistory, @PathVariable("id") int ID) {
        Optional<Request> getRequest = requestRepository.findById(ID);
        if (!getRequest.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Cannot find this request"), HttpStatus.NOT_FOUND);
        }
        requestHistory.setRequestID(getRequest.get().getRequestID());
        requestHistoryService.save(requestHistory);
        return new ResponseEntity<>(requestHistory, HttpStatus.OK);
    }
/*

    //detail 1 request
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailRecentRequest(@PathVariable("id") int requestId) {
        List<RequestHistory> lsStatusRequest = requestHistoryService.listRecentStatus(requestId);
        //select recently status by createdTime
        RequestHistory requestHistory = lsStatusRequest.get(0);
        //String status = requestHistory.getStatus();
        DetailRequestModel detailRequestModel = new DetailRequestModel();

        Optional<RequestHistory> getRequestAcceptedNurse = requestHistoryRepository.findByRequestIDAndStatus(requestHistory.getRequestID(),"accepted");
        if (!getRequestAcceptedNurse.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Request hasnot been accepted by any nurse!"), HttpStatus.NOT_FOUND);
        }
        //get nurse information
        detailRequestModel.setNurseID(getRequestAcceptedNurse.get().getUserID());
        Optional<User> getNurse = userRepository.findById(getRequestAcceptedNurse.get().getUserID());
        if (!getNurse.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Nurse not found"), HttpStatus.BAD_REQUEST);
        }
        detailRequestModel.setNurseName(getNurse.get().getName());
        Optional<RequestHistory> getRequestAcceptedByCoordinator = requestHistoryRepository.findByRequestIDAndStatus(requestHistory"waitingforresult");
        if (!getRequestAcceptedByCoordinator.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Request hasnot been received by any coordinator!"), HttpStatus.NOT_FOUND);
        }
        //get coordinator information
        detailRequestModel.setCoordinatorID(getRequestAcceptedByCoordinator.get().getUserID());
        Optional<User> getCoordinator = userRepository.findById(getRequestAcceptedByCoordinator.get().getUserID());
        if (!getCoordinator.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Coordinator not found"), HttpStatus.BAD_REQUEST);
        }
        detailRequestModel.setCoordinatorName(getCoordinator.get().getName());

        return new ResponseEntity<>(detailRequestModel, HttpStatus.OK);
    }

*/

}
