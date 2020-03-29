package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.DetailRequestModel;
import com.edu.fpt.medtest.model.RequestModel;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.service.FileStorageService;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.Request.RequestTestService;
import com.edu.fpt.medtest.service.Request.ResultService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RequestTestService requestTestService;

    @Autowired
    private RequestTestRepository requestTestRepository;

    @Autowired
    private ResultService resultService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/create")
    public ResponseEntity<?> createNewRequest(@RequestBody RequestModel requestModel) {
        Request request = new Request();
        request.setUserID(requestModel.getUserID());
        request.setMeetingTime(requestModel.getMeetingTime());
        request.setCreatedDate(requestModel.getCreatedTime());
        request.setAddress(requestModel.getAddress());
        request.setTownCode(requestModel.getTownCode());
        request.setDistrictCode(requestModel.getDistrictCode());

        //parse (String)testID into (Integer)testID and check if test available
        List<String> selectedTests = requestModel.getSelectedTest();
        int selectedTestID;
        List<Integer> listSelectedTestID = new ArrayList<>();
        for (String selectedTest : selectedTests) {
            selectedTestID = Integer.parseInt(selectedTest);
            boolean existedTest = testRepository.existsByTestID(selectedTestID);
            if (existedTest == true) {
                listSelectedTestID.add(selectedTestID);
            } else {
                return new ResponseEntity<>(new ApiResponse(true, "Not found this test"), HttpStatus.NOT_FOUND);
            }
        }

        //foreach available test, find and add tests by its ID
        List<RequestTest> lsRequestTest = new ArrayList<>();
        for (Integer selectingTestID : listSelectedTestID) {
            requestRepository.save(request);
            RequestTest requestTest = new RequestTest();
            requestTest.setRequestID(request.getRequestID());
            requestTest.setTestID(selectingTestID);
            lsRequestTest.add(requestTest);
        }
        System.out.println(lsRequestTest);
        requestTestService.saveListRequestTest(lsRequestTest);

        //get list chosen test
        long testAmount = 0;
        List<String> lsChosenTest = new ArrayList<>();
        for (RequestTest requestTest : lsRequestTest) {
            String chosenTest = String.valueOf(testRepository.findById(requestTest.getTestID()).get().getTestID());
            testAmount += testRepository.findById(requestTest.getTestID()).get().getPrice();
            lsChosenTest.add(chosenTest);
        }
        System.out.println(lsChosenTest);


        //return detail
        DetailRequestModel detailRequestModel = new DetailRequestModel();
        detailRequestModel.setRequestID(String.valueOf(request.getRequestID()));
        detailRequestModel.setCustomerID(String.valueOf(userRepository.findById(request.getUserID()).get().getId()));
        detailRequestModel.setCustomerName(userRepository.findById(request.getUserID()).get().getName());
        detailRequestModel.setCustomerPhoneNumber(userRepository.findById(request.getUserID()).get().getPhoneNumber());
        detailRequestModel.setCustomerDOB(userRepository.findById(request.getUserID()).get().getDob());
        detailRequestModel.setRequestAddress(request.getAddress() + " " + townRepository.getOne(request.getTownCode()).getTownName()
                + " " + districtRepository.getOne(request.getDistrictCode()).getDistrictName());
        detailRequestModel.setRequestMeetingTime(request.getMeetingTime());
        detailRequestModel.setRequestCreatedTime(request.getCreatedDate());
        detailRequestModel.setRequestStatus("pending");
        detailRequestModel.setLsSelectedTest(lsChosenTest);
        detailRequestModel.setRequestAmount(String.valueOf(testAmount));
        detailRequestModel.setRequestNote("Just created!");
        return new ResponseEntity<>(detailRequestModel, HttpStatus.OK);
    }

    /*@PostMapping("/create")
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
        ================infinitely list but still insert successfully to database???====================
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
        =========================================================
        return new ResponseEntity<>(request, HttpStatus.OK);
    }*/

    //update status of 1 request - add into request_history table
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateRequestStatus(@RequestBody RequestHistory requestHistory, @PathVariable("id") int ID) {
        Optional<Request> getRequest = requestRepository.findById(ID);
        if (!getRequest.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Cannot find this request"), HttpStatus.NOT_FOUND);
        }
        requestHistory.setRequestID(getRequest.get().getRequestID());
        requestHistoryService.save(requestHistory);
        Notification notification = new Notification();
        notification.setUserID(requestRepository.findById(ID).get().getUserID());
        notification.setRequestID(ID);
        notification.setAppointmentID(1);
        notification.setIsRead(0);
        notification.setType("REQUEST");
        notification.setMessage("RequestID = " + ID +
                + requestHistoryRepository.getOne(requestHistory.getRequestHistoryID()).getRequestHistoryID()
                + " have changed status into: "
                + requestHistoryRepository.getOne(requestHistory.getRequestHistoryID()).getStatus() );
        notificationService.saveNoti(notification);
        return new ResponseEntity<>(requestHistory, HttpStatus.OK);
    }

    //detail 1 request
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getDetailRecentRequest(@PathVariable("id") int requestId) {
        //Object will return as a request detail
        DetailRequestModel detailRequestModel = new DetailRequestModel();

        //Check if request existed
        boolean existedRequest = requestRepository.existsByRequestID(requestId);
        if (existedRequest == false) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no request with ID = " + requestId), HttpStatus.NOT_FOUND);
        }

        //Get all status of the request with ID with descending created time
        List<RequestHistory> lsStatusRequest = requestHistoryService.listRecentStatus(requestId);

        //check if request has no update yet (status = pending) -> a recently created request
        if (lsStatusRequest.isEmpty()) {
            Request newCreatedRequest = requestRepository.getOne(requestId);
            Optional<User> newCreatedRequestUser = userRepository.findById(newCreatedRequest.getUserID());
            Town newCreatedRequestTown = townRepository.getOne(newCreatedRequest.getTownCode());
            District newCreatedRequestDistrict = districtRepository.getOne(newCreatedRequest.getDistrictCode());
            detailRequestModel.setRequestID(String.valueOf(requestId)); //requestID
            detailRequestModel.setCustomerID(String.valueOf(newCreatedRequest.getUserID())); //customerID
            detailRequestModel.setCustomerName(newCreatedRequestUser.get().getName()); //customerName
            detailRequestModel.setCustomerPhoneNumber(newCreatedRequestUser.get().getPhoneNumber());//customerPhoneNumber
            detailRequestModel.setCustomerDOB(newCreatedRequestUser.get().getDob()); //customerDOB
            detailRequestModel.setRequestAddress(newCreatedRequest.getAddress() + " " + newCreatedRequestTown.getTownName() + " " + newCreatedRequestDistrict.getDistrictName()); //customer full address
            detailRequestModel.setRequestMeetingTime(newCreatedRequest.getMeetingTime()); //meeting time
            detailRequestModel.setRequestCreatedTime(newCreatedRequest.getCreatedDate()); //created time
            detailRequestModel.setNurseID("NOT HAVE ANY NURSE YET!");
            detailRequestModel.setNurseName("NOT HAVE ANY NURSE YET!");
            detailRequestModel.setCoordinatorID("NOT HAVE ANY COORDINATOR YET!");
            detailRequestModel.setCoordinatorName("NOT HAVE ANY COORDINATOR YET!");
            detailRequestModel.setRequestStatus("pending"); //status
            //set list selected test
            List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(requestId);
            List<String> lsTestID = new ArrayList<>();
            long testAmount = 0;
            for (RequestTest tracking : lsRequestTests) {
                System.out.println(tracking.getTestID());
                String testID = String.valueOf(tracking.getTestID());
                testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                lsTestID.add(testID);
            }
            detailRequestModel.setLsSelectedTest(lsTestID);
            //set amount of test
            detailRequestModel.setRequestAmount(String.valueOf(testAmount));
            //set note
            detailRequestModel.setRequestNote("Just created");
        } else {
            //Get the latest status of request
            RequestHistory requestHistory = lsStatusRequest.get(0);

            //get the latest status which status = accepted -> find nurse
            List<RequestHistory> getListRequestAcceptedNurse =
                    requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "accepted");
            if (getListRequestAcceptedNurse.isEmpty() || requestHistory.getStatus().equals("pending")) {
                detailRequestModel.setNurseID("NOT HAVE ANY NURSE YET!");
                detailRequestModel.setNurseName("NOT HAVE ANY NURSE YET!");
            } else {
                //get nurse ID
                detailRequestModel.setNurseID(String.valueOf(getListRequestAcceptedNurse.get(0).getUserID()));
                //get nurse name
                detailRequestModel.setNurseName(userRepository.findById(getListRequestAcceptedNurse.get(0).getUserID()).get().getName());
            }

            //get the latest status which status = waitingforresult -> find coordinator
            List<RequestHistory> getListRequestAcceptedCoordinator =
                    requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "waitingforresult");
            if (getListRequestAcceptedCoordinator.isEmpty() || requestHistory.getStatus().equals("pending")) {
                detailRequestModel.setCoordinatorID("NOT HAVE ANY COORDINATOR YET!");
                detailRequestModel.setCoordinatorName("NOT HAVE ANY COORDINATOR YET!");
            } else {
                //get coordinator ID
                detailRequestModel.setCoordinatorID(String.valueOf(getListRequestAcceptedCoordinator.get(0).getUserID()));
                //get coordinator name
                detailRequestModel.setCoordinatorName(userRepository.findById(getListRequestAcceptedCoordinator.get(0).getUserID()).get().getName());
            }

            Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());

            //return detail request
            detailRequestModel.setRequestStatus(requestHistory.getStatus());
            detailRequestModel.setRequestID(String.valueOf(nowRequest.getRequestID()));
            detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
            detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
            detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
            detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
            detailRequestModel.setRequestAddress(nowRequest.getAddress() + " " + townRepository.findById(nowRequest.getTownCode()).get().getTownName()
                    + " " + districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
            detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
            detailRequestModel.setRequestCreatedTime(nowRequest.getCreatedDate());
            // get list test
            List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
            List<String> lsTestID = new ArrayList<>();
            long testAmount = 0;
            for (RequestTest tracking : lsRequestTests) {
                System.out.println(tracking.getTestID());
                String testID = String.valueOf(tracking.getTestID());
                testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                lsTestID.add(testID);
            }
            detailRequestModel.setLsSelectedTest(lsTestID);
            //set amount of test
            detailRequestModel.setRequestAmount(String.valueOf(testAmount));
            //setNote
            detailRequestModel.setRequestNote(requestHistory.getNote());

        }
        return new ResponseEntity<>(detailRequestModel, HttpStatus.OK);
    }

    //get list result of a request
    @GetMapping("/detail/{id}/result")
    public ResponseEntity<?> getListResult(@PathVariable("id") int requestID) {
        Optional<Request> request = requestRepository.findById(requestID);
        if (!request.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no request with ID = " + requestID), HttpStatus.NOT_FOUND);
        }
        List<Result> lsResult = resultService.lsResultByRequestID(requestID);
        if (lsResult.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No result for this request"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsResult, HttpStatus.OK);
    }

    //save result 1 request
    //////////////////// get file?
    @PostMapping("/detail/{id}/save-result")
    public ResponseEntity<?> saveResult(@RequestBody Result result, @PathVariable("id") int requestID,@RequestParam("file") MultipartFile file) {
        result.setRequestID(requestID);
        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/saveFile/")
                .path(fileName)
                .toUriString();
        result.setImage(fileDownloadUri);
        resultService.saveResult(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
