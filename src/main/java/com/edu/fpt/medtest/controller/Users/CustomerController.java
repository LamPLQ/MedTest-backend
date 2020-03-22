package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.ChangePasswordModel;
import com.edu.fpt.medtest.model.DetailRequestModel;
import com.edu.fpt.medtest.model.UserAppointment;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.Request.RequestTestService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.edu.fpt.medtest.utils.EncodePassword.getSHA;
import static com.edu.fpt.medtest.utils.EncodePassword.toHexString;

@RestController
@RequestMapping("/users/customers")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestHistoryService requestHistoryService;

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
    private TestRepository testRepository;

    //customer register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User customer) throws NoSuchAlgorithmException {
      /*List<User> users = userService.getListUser();
        for (User userTrack : users) {
            if (userTrack.getPhoneNumber().equals(customer.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
            }
        }*/
        boolean existByPhoneNumber = userRepository.existsByPhoneNumber(customer.getPhoneNumber());
        if (existByPhoneNumber == true) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
        }
        customer.setActive(0);
        customer.setAddress(null);
        customer.setRole("CUSTOMER");
        customer.setImage(customer.getImage());
        customer.setTownCode(null);
        customer.setDistrictCode(null);
        customer.setPassword(toHexString(getSHA(customer.getPassword())));
        userService.saveUser(customer);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
    }

    //list all customer
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("CUSTOMER");
        System.out.println(users);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get customer - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update customer info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User customer, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        customer.setId(id);
        userService.update(customer);
        return new ResponseEntity<>(new ApiResponse(true, "Update user successfully"), HttpStatus.OK);
    }

    //view list appointment theo 1 customer
    @GetMapping("/{id}/appointments/list")
    public ResponseEntity<?> getListAppointment(@PathVariable("id") int id) {
        List<Appointment> lsAppointmentCustomer = appointmentRepository.findAllByCustomerID(id);
//        UserAppointment userAppointment = new UserAppointment();
        User userAppoint = userService.findUserByID(id).get();
        if (lsAppointmentCustomer.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        }
        //list detail of each appoinment which belong to user
        List<UserAppointment> listUserAppoinment = new ArrayList<>();
        for (Appointment appointments : lsAppointmentCustomer) {
            UserAppointment userAppointment = new UserAppointment();
            //userAppointment.setAppointment_coordinatorName("" + appointments.getCoordinatorID());
            userAppointment.setAppointment_id(userAppoint.getId());
            userAppointment.setAppointment_customerName(userAppoint.getName());
            userAppointment.setAppointment_phoneNumber(userAppoint.getPhoneNumber());
            userAppointment.setAppointment_DOB(userAppoint.getDob());
            userAppointment.setAppointment_status(appointments.getStatus());
            userAppointment.setAppointment_note(appointments.getNote());
            userAppointment.setAppointment_meetingTime(appointments.getMeetingTime());
            userAppointment.setAppointment_createdTime(appointments.getCreatedTime());
            listUserAppoinment.add(userAppointment);
        }
        return new ResponseEntity<>(listUserAppoinment, HttpStatus.OK);
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) throws NoSuchAlgorithmException {
        Optional<User> getCustomer = userService.findUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getCustomer.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        if (!getCustomer.get().getPassword().equals(toHexString(getSHA(changePasswordModel.getOldPassword())))) {
            return new ResponseEntity<>(new ApiResponse(true, "Incorrect current password"), HttpStatus.BAD_REQUEST);
        }
        changePasswordModel.setID(id);
        getCustomer.get().setPassword(toHexString(getSHA(changePasswordModel.getNewPassword())));
        userService.saveUser(getCustomer.get());
        return new ResponseEntity<>(new ApiResponse(true, "Change password successfully!"), HttpStatus.OK);
    }

    //list request of customer
    @GetMapping("{id}/requests/list")
    public ResponseEntity<?> lsRequestOfUser(@PathVariable("id") int userID) {
        List<Request> lsRequest = requestService.getListByUser(userID);
        if (lsRequest.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Customer has not had any request yet!"), HttpStatus.NOT_FOUND);
        }
        List<DetailRequestModel> lsDRequestDetail = new ArrayList<>();
        for (Request eachRequest : lsRequest) {
            int requestId = eachRequest.getRequestID();
            //===========================================
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
                detailRequestModel.setRequestID(requestId); //requestID
                detailRequestModel.setCustomerID(newCreatedRequest.getUserID()); //customerID
                detailRequestModel.setCustomerName(newCreatedRequestUser.get().getName()); //customerName
                detailRequestModel.setCustomerPhoneNumber(newCreatedRequestUser.get().getPhoneNumber());//customerPhoneNumber
                detailRequestModel.setCustomerDOB(newCreatedRequestUser.get().getDob()); //customerDOB
                detailRequestModel.setCustomerAddress(newCreatedRequest.getAddress() + " " + newCreatedRequestTown.getTownName() + " " + newCreatedRequestDistrict.getDistrictName()); //customer full address
                detailRequestModel.setRequestMeetingTime(newCreatedRequest.getMeetingTime()); //meeting time
                detailRequestModel.setRequestCreatedTime(newCreatedRequest.getCreatedDate()); //created time
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
                detailRequestModel.setRequestAmount(testAmount);
                //set note
                detailRequestModel.setRequestNote("Just created!");

            } else {
                //Get the latest status of request
                RequestHistory requestHistory = lsStatusRequest.get(0);

                //get the latest status which status = accepted -> find nurse
                List<RequestHistory> getListRequestAcceptedNurse =
                        requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "accepted");
                if (getListRequestAcceptedNurse.isEmpty() || requestHistory.getStatus().equals("pending")) {
                    detailRequestModel.setNurseName("NOT HAVE ANY NURSE YET!");
                } else {
                    //get nurse ID
                    detailRequestModel.setNurseID(getListRequestAcceptedNurse.get(0).getUserID());
                    //get nurse name
                    detailRequestModel.setNurseName(userRepository.findById(getListRequestAcceptedNurse.get(0).getUserID()).get().getName());
                }

                //get the latest status which status = waitingforresult -> find coordinator
                List<RequestHistory> getListRequestAcceptedCoordinator =
                        requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "waitingforresult");
                if (getListRequestAcceptedCoordinator.isEmpty() || requestHistory.getStatus().equals("pending")) {
                    detailRequestModel.setCoordinatorID(0);
                    detailRequestModel.setCoordinatorName("NOT HAVE ANY COORDINATOR YET!");
                } else {
                    //get coordinator ID
                    detailRequestModel.setCoordinatorID(getListRequestAcceptedCoordinator.get(0).getUserID());
                    //get coordinator name
                    detailRequestModel.setCoordinatorName(userRepository.findById(getListRequestAcceptedCoordinator.get(0).getUserID()).get().getName());
                }

                Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());

                //return detail request
                detailRequestModel.setRequestStatus(requestHistory.getStatus());
                detailRequestModel.setRequestID(nowRequest.getRequestID());
                detailRequestModel.setCustomerID(nowRequest.getUserID());
                detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                detailRequestModel.setCustomerAddress(nowRequest.getAddress() + " " + townRepository.findById(nowRequest.getTownCode()).get().getTownName()
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
                detailRequestModel.setRequestAmount(testAmount);
                //set note
                detailRequestModel.setRequestNote(requestHistory.getNote());
            }
            //===========================================
            lsDRequestDetail.add(detailRequestModel);
        }
        return new ResponseEntity<>(lsDRequestDetail, HttpStatus.OK);
    }
}
