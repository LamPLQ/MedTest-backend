package com.edu.fpt.medtest.controller.Users;


import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.ChangePasswordModel;
import com.edu.fpt.medtest.model.CompletedRequestModel;
import com.edu.fpt.medtest.model.DetailRequestModel;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/nurses")
public class NurseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestHistoryService requestHistoryService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RequestTestRepository requestTestRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //nurse register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User nurse) {
        boolean existByPhoneNumber = userRepository.existsByPhoneNumber(nurse.getPhoneNumber());
        if (existByPhoneNumber == true) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
        }
        nurse.setActive(1);
        nurse.setAddress(null);
        nurse.setRole("NURSE");
        nurse.setImage(nurse.getImage());
        nurse.setTownCode(null);
        nurse.setDistrictCode(null);
        nurse.setPassword(bCryptPasswordEncoder.encode(nurse.getPassword()));
        userService.saveUser(nurse);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
    }

    //list all nurse
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("NURSE");
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get nurse - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("NURSE")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update nurse info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User nurse, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Nurse not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("NURSE")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        nurse.setId(id);
        userService.update(nurse);
        return new ResponseEntity<>(new ApiResponse(true, "Update nurse successfully"), HttpStatus.OK);
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) {
        Optional<User> getNurse = userService.findUserByID(id);
        if (!getNurse.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getNurse.get().getRole().equals("NURSE")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getNurse.get().getPassword())) {
            return new ResponseEntity<>(new ApiResponse(true, "Incorrect current password"), HttpStatus.BAD_REQUEST);
        }
        changePasswordModel.setID(id);
        getNurse.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
        userService.saveUser(getNurse.get());
        return new ResponseEntity<>(new ApiResponse(true, "Change password successfully!"), HttpStatus.OK);
    }

    //Screen "Tìm đơn xét nghiệm"
    //List status = pending + coordinatorlostsample
    @GetMapping("/find-request")
    public ResponseEntity<?> findRequest() {

        // list request will return for nurse
        List<DetailRequestModel> lsFindingRequest = new ArrayList<>();

        /*
        List request just be created (status = "pending", have no record in request_history table)
        */

        //List by created time desc
        //List<Request> lsAllRequest = requestRepository.findAllByOrderByCreatedDateDesc();

        //List all created request
        List<Request> lsAllRequest = requestService.lsRequest();
        //with each request in list request
        for (Request requestPending : lsAllRequest) {
            DetailRequestModel detailRequestModel = new DetailRequestModel();
            int requestId = requestPending.getRequestID();

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
                detailRequestModel.setRequestStatus("pending"); //status
                //set list selected test
                List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(requestId);
                List<String> lsTestID = new ArrayList<>();
                long testAmount = 0;
                for (RequestTest tracking : lsRequestTests) {
                    String testID = String.valueOf(tracking.getTestID());
                    testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                    lsTestID.add(testID);
                }
                detailRequestModel.setLsSelectedTest(lsTestID);
                //set amount of test
                detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                //set note
                detailRequestModel.setRequestNote("Just created!");
                lsFindingRequest.add(detailRequestModel);
            }
            /*
            List request in request_history (status = "pending/coordinatorlostsample" in the last status)
            */
            else {
                //Get the latest status of request
                RequestHistory requestHistory = lsStatusRequest.get(0);
                //System.out.println(requestId + " " + requestHistory.getStatus());
                if (requestHistory.getStatus().equals("pending")) {
                    //get nurse ID
                    //detailRequestModel.setNurseID(String.valueOf(requestHistory.getUserID()));
                    detailRequestModel.setNurseID("null");
                    //get nurse name
                    //detailRequestModel.setNurseName(userRepository.findById(requestHistory.getUserID()).get().getName());
                    detailRequestModel.setNurseName("NOT HAVE ANY NURSE YET");
                    Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());

                    //return detail request
                    detailRequestModel.setRequestStatus(requestHistory.getStatus());
                    detailRequestModel.setRequestID(String.valueOf(nowRequest.getRequestID()));
                    detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
                    detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                    detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                    detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                    detailRequestModel.setRequestAddress(nowRequest.getAddress() + " " + townRepository.findById(nowRequest.getTownCode()).get().getTownName() + " " + districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                    detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
                    detailRequestModel.setRequestCreatedTime(nowRequest.getCreatedDate());
                    // get list test
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                    List<String> lsTestID = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                    }
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote(requestHistory.getNote());
                    lsFindingRequest.add(detailRequestModel);
                }

                //get the latest status which status = coordinatorlostsample
                if (requestHistory.getStatus().equals("coordinatorlostsample")) {
                    //get coordinator ID
                    detailRequestModel.setCoordinatorID(String.valueOf(requestHistory.getUserID()));
                    //get coordinator name
                    detailRequestModel.setCoordinatorID(String.valueOf(requestHistory.getUserID()));

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
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                    }
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote(requestHistory.getNote());
                    lsFindingRequest.add(detailRequestModel);
                }
            }
        }
        if (lsFindingRequest.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "NO RECENTLY REQUEST"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lsFindingRequest, HttpStatus.OK);
    }

    //Screen "Đơn đang nhận"
    //status = {accepted, lostsample, transporting }
    @GetMapping("{id}/list/handling")
    public ResponseEntity<?> lsHandling(@PathVariable("id") int nurseID) {
        //list handling return
        List<DetailRequestModel> lsNurseHandling = new ArrayList<>();

        //List all created request
        List<Request> lsAllRequest = requestService.lsRequest();
        //with each request in list request
        List<RequestHistory> lsLastStatus = new ArrayList<>();
        for (Request requestPending : lsAllRequest) {
            boolean existRequestID = requestHistoryRepository.existsByRequestID(requestPending.getRequestID());
            if (existRequestID == false) {
                System.out.println("Dont have this requestID");
            } else {
                //System.out.println("OK");
                RequestHistory requestAvailable = requestHistoryRepository.findByRequestIDOrderByCreatedTimeDesc(requestPending.getRequestID()).get(0);
                //System.out.println(requestAvailable.getNote());
                lsLastStatus.add(requestAvailable);
            }
        }
        for (RequestHistory request : lsLastStatus) {
            if ((request.getUserID() == nurseID && (request.getStatus().equals("accepted"))) ||
                    (request.getUserID() == nurseID && (request.getStatus().equals("lostsample"))) ||
                    (request.getUserID() == nurseID && (request.getStatus().equals("transporting")))) {
                DetailRequestModel detail = new DetailRequestModel();
                //requestID
                detail.setRequestID(String.valueOf(request.getRequestID()));
                //nurseID
                detail.setNurseID(String.valueOf(request.getUserID()));
                //nurseName
                detail.setNurseName(userRepository.findById(request.getUserID()).get().getName());
                //request of request history
                Request recentRequest = requestRepository.getOne(request.getRequestID());
                ///////////////////////
                //customerID
                detail.setCustomerID(String.valueOf(recentRequest.getUserID()));//
                //customerName
                detail.setCustomerName(userRepository.findById(recentRequest.getUserID()).get().getName());//
                //customer phoneNumber
                detail.setCustomerPhoneNumber(userRepository.findById(recentRequest.getUserID()).get().getPhoneNumber());//
                //customer DOB
                detail.setCustomerDOB(userRepository.findById(recentRequest.getUserID()).get().getDob());//
                //request Address
                detail.setRequestAddress(recentRequest.getAddress() + " " +
                        townRepository.findById(recentRequest.getTownCode()).get().getTownName() + " " +
                        districtRepository.findById(recentRequest.getDistrictCode()).get().getDistrictName());
                //request meetingTime
                detail.setRequestMeetingTime(recentRequest.getMeetingTime());
                //request status
                detail.setRequestStatus(request.getStatus());
                //request created time
                detail.setRequestCreatedTime(recentRequest.getCreatedDate());
                //coordinator
                detail.setCoordinatorID("NOT HAVE ANY COORDINATOR YET");
                detail.setCoordinatorName("NOT HAVE ANY COORDINATOR YET");
                //requestNote
                detail.setRequestNote(request.getNote());
                List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(recentRequest.getRequestID());
                List<String> lsTestID = new ArrayList<>();
                long testAmount = 0;
                for (RequestTest tracking : lsRequestTests) {
                    String testID = String.valueOf(tracking.getTestID());
                    testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                    lsTestID.add(testID);
                }
                detail.setLsSelectedTest(lsTestID);
                //set amount of test
                detail.setRequestAmount(String.valueOf(testAmount));
                lsNurseHandling.add(detail);
            }
        }
        if (lsNurseHandling.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no handling request for nurseID = " + nurseID), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsNurseHandling, HttpStatus.OK);
    }

    //Screen "Lịch sử nhận đơn"
    //status = {closed, waitingforresult }
    @GetMapping("{id}/list/request-completed")
    public ResponseEntity<?> lsCompleted(@PathVariable("id") int nurseID) {
        //list completed
        List<CompletedRequestModel> lsCompletedReqs = new ArrayList<>();

        //List all created request
        List<Request> lsAllRequest = requestService.lsRequest();
        //with each request in list request
        List<RequestHistory> lsLastStatus = new ArrayList<>();
        for (Request requestPending : lsAllRequest) {
            boolean existRequestID = requestHistoryRepository.existsByRequestID(requestPending.getRequestID());
            if (existRequestID == false) {
                System.out.println("Dont have this requestID");
            } else {
                //System.out.println("OK");
                RequestHistory requestAvailable = requestHistoryRepository.findByRequestIDOrderByCreatedTimeDesc(requestPending.getRequestID()).get(0);
                //System.out.println(requestAvailable.getNote());
                lsLastStatus.add(requestAvailable);
            }
        }
        for (RequestHistory request : lsLastStatus) {
            if (request.getStatus().equals("closed") || request.getStatus().equals("waitingforresult")) {
                //list of "accepted" status with each request history
                boolean existByRequestIDAndStatusAccepted = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "accepted", nurseID);
                if (existByRequestIDAndStatusAccepted == false) {
                    System.out.println("No request history with /accepted/ status");
                } else {
                    RequestHistory acceptedStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "accepted", nurseID).get(0);
                    //System.out.println(acceptedStatusRequest.getRequestID() + acceptedStatusRequest.getNote());
                    boolean existByRequestIDAndStatusTransporting = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "transporting", nurseID);
                    if (existByRequestIDAndStatusTransporting == false) {
                        System.out.println("No request history with /transporting/ status");
                    } else {
                        RequestHistory transportingStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "transporting", nurseID).get(0);
                        //System.out.println(transportingStatusRequest.getRequestID() + transportingStatusRequest.getNote());
                        CompletedRequestModel completedRequestModel = new CompletedRequestModel();
                        completedRequestModel.setRequestID(String.valueOf(request.getRequestID()));
                        ////////////////////Object Request
                        Request workingRequest = requestRepository.getOne(request.getRequestID());
                        ////////////////////
                        completedRequestModel.setCustomerID(String.valueOf(workingRequest.getUserID()));
                        completedRequestModel.setCustomerName(userRepository.findById(workingRequest.getUserID()).get().getName());
                        completedRequestModel.setCustomerPhoneNumber(userRepository.findById(workingRequest.getUserID()).get().getPhoneNumber());
                        completedRequestModel.setCustomerDOB(userRepository.findById(workingRequest.getUserID()).get().getDob());
                        completedRequestModel.setRequestAddress(workingRequest.getAddress() + " " +
                                townRepository.findById(workingRequest.getTownCode()).get().getTownName() + " " +
                                districtRepository.findById(workingRequest.getDistrictCode()).get().getDistrictName());
                        completedRequestModel.setRequestMeetingTime(workingRequest.getMeetingTime());
                        completedRequestModel.setRequestCreatedTime(workingRequest.getCreatedDate());
                        completedRequestModel.setNurseID(String.valueOf(nurseID));
                        completedRequestModel.setNurseName(userRepository.findById(nurseID).get().getName());
                        completedRequestModel.setCoordinatorID(String.valueOf(request.getUserID()));
                        completedRequestModel.setCoordinatorName(userRepository.findById(request.getUserID()).get().getName());
                        completedRequestModel.setRequestStatus(request.getStatus());
                        completedRequestModel.setRequestNote(request.getNote());
                        completedRequestModel.setRequestAcceptedTime(acceptedStatusRequest.getCreatedTime());
                        completedRequestModel.setRequestTransportingTime(transportingStatusRequest.getCreatedTime());
                        List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(workingRequest.getRequestID());
                        List<String> lsTestID = new ArrayList<>();
                        long testAmount = 0;
                        for (RequestTest tracking : lsRequestTests) {
                            String testID = String.valueOf(tracking.getTestID());
                            testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                            lsTestID.add(testID);
                        }
                        completedRequestModel.setLsSelectedTest(lsTestID);
                        //set amount of test
                        completedRequestModel.setRequestAmount(String.valueOf(testAmount));
                        lsCompletedReqs.add(completedRequestModel);
                    }
                }
            }
        }
        if (lsCompletedReqs.isEmpty()) {
            return new ResponseEntity(new ApiResponse(true, "Have nó request history for nurseID = " + nurseID), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsCompletedReqs, HttpStatus.OK);

    }
}

