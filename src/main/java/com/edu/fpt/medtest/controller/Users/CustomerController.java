package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.*;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.security.SecurityUtils;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.CheckEmailExist;
import com.edu.fpt.medtest.utils.ComfirmResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private RequestHistoryService requestHistoryService;

    @Autowired
    private RequestHistoryRepository requestHistoryRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RequestTestRepository requestTestRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CheckEmailExist checkEmailExist;

    //Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel loginUser) {
        boolean existByPhoneNumberAndRole = userRepository.existsByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
        if (!existByPhoneNumberAndRole == true) {
            return new ResponseEntity<>(new ApiResponse(true, "Người dùng không tồn tại!"), HttpStatus.OK);
        }
        User userLogin = userRepository.getUserByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
        //check active
        if (userLogin.getActive() == 0) {
            return new ResponseEntity<>(new ApiResponse(false, "Số tài khoản đã bị khoá"), HttpStatus.OK);
        }

        //check password
        if (!BCrypt.checkpw(loginUser.getPassword(), userLogin.getPassword())) {
            return new ResponseEntity<>(new ApiResponse(false, "Sai mật khẩu!"), HttpStatus.OK);
        }
        //create BEARER token
        String token = Jwts.builder()
                .setSubject(loginUser.getPhoneNumber())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityUtils.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityUtils.SECRET.getBytes())
                .compact();

        //return current user
        User successfulUser = (userRepository.getUserByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole()));
        LoginAccountModel loginAccountModel = new LoginAccountModel();
        loginAccountModel.setUserInfo(successfulUser);
        loginAccountModel.setToken(token);
        return new ResponseEntity<>(loginAccountModel, HttpStatus.OK);
    }

    //customer register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User customer) {
        boolean existByPhoneAndRole = userRepository.existsByPhoneNumberAndRole(customer.getPhoneNumber(), "CUSTOMER");
        if (existByPhoneAndRole == true) {
            return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại đã tồn tại!"), HttpStatus.OK);
        }
       /* boolean emailExist = checkEmailExist.isAddressValid(customer.getEmail());
        if(emailExist==false){
            return new ResponseEntity<>(new ApiResponse(false,"Email không tồn tại!"), HttpStatus.OK);
        }*/
        String enCryptPassword = bCryptPasswordEncoder.encode(customer.getPassword());
        customer.setActive(1);
        customer.setAddress(null);
        customer.setRole("CUSTOMER");
        customer.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
        customer.setTownCode(null);
        customer.setDistrictCode(null);
        customer.setPassword(enCryptPassword);
        userService.saveUser(customer);
        return new ResponseEntity<>(new ApiResponse(true, "Đã đăng kí thành công!"), HttpStatus.OK);
    }

    //list all customer
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("CUSTOMER");
        System.out.println(users);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào trong danh sách hiện tại."), HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get customer - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getCustomer = userService.getUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(getCustomer, HttpStatus.OK);
    }

    //update customer info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User customer, @PathVariable("id") int id) {
        Optional<User> getCustomer = userService.getUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
        }
        customer.setId(id);
        userService.update(customer);
        return new ResponseEntity<>(getCustomer, HttpStatus.OK);
    }

    //update customer address
    @PutMapping("/detail/address/update/{id}")
    public ResponseEntity<?> updateAddressUser(@RequestBody User customer, @PathVariable("id") int id) {
        Optional<User> getCustomer = userService.getUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
        }
        customer.setId(id);
        userService.updateAddress(customer);
        return new ResponseEntity<>(getCustomer, HttpStatus.OK);
    }

    //view list appointment theo 1 customer
    @GetMapping("/{id}/appointments/list")
    public ResponseEntity<?> getListAppointment(@PathVariable("id") int id) {
        try {
            List<Appointment> lsAppointmentCustomer = appointmentRepository.findAllByCustomerID(id);
            User userAppoint = userService.findUserByID(id).get();
            if (lsAppointmentCustomer.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại chưa có lịch hẹn!"), HttpStatus.OK);
            }
            //list detail of each appoinment which belong to user
            List<UserAppointmentModel> listUserAppoinment = new ArrayList<>();
            for (Appointment appointment : lsAppointmentCustomer) {
                UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
                userAppointmentModel.setAppointment_id(appointment.getID());
                userAppointmentModel.setAppointment_customerName(userAppoint.getName());
                userAppointmentModel.setAppointment_phoneNumber(userAppoint.getPhoneNumber());
                userAppointmentModel.setAppointment_DOB(userAppoint.getDob());
                userAppointmentModel.setAppointment_status(appointment.getStatus());
                userAppointmentModel.setAppointment_note(appointment.getNote());
                userAppointmentModel.setAppointment_meetingTime(appointment.getMeetingTime());
                //=====================//
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf2.format(appointment.getCreatedTime());
                String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//
                userAppointmentModel.setAppointment_createdTime(createdTime);
                listUserAppoinment.add(userAppointmentModel);
            }
            return new ResponseEntity<>(listUserAppoinment, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang tải. Vui lòng thử lại!"), HttpStatus.OK);
        }
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) {
        Optional<User> getCustomer = userService.getUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ComfirmResponse(true, "Người dùng không tồn tại!", false), HttpStatus.OK);
        }
        if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getCustomer.get().getPassword())) {
            return new ResponseEntity<>(new ComfirmResponse(true, "Mật khẩu hiện tại không đúng!", false), HttpStatus.OK);
        }
        changePasswordModel.setID(id);
        getCustomer.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
        userService.saveUser(getCustomer.get());
        return new ResponseEntity<>(new ComfirmResponse(true, "Thay đổi mật khẩu thành công!", true), HttpStatus.OK);
    }

    //list request of customer
    @GetMapping("{id}/requests/list")
    public ResponseEntity<?> lsRequestOfUser(@PathVariable("id") int userID) {
        try {
            List<Request> lsRequest = requestService.getListByUser(userID);
            if (lsRequest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Hiện tại không có yêu cầu nào từ khách hàng!"), HttpStatus.OK);
            }
            List<DetailRequestModel> lsDRequestDetail = new ArrayList<>();
            for (Request eachRequest : lsRequest) {
                String requestId = eachRequest.getRequestID();
                //===========================================
                //Object will return as a request detail
                DetailRequestModel detailRequestModel = new DetailRequestModel();
                //Get all status of the request with ID with descending created time
                List<RequestHistory> lsStatusRequest = requestHistoryService.listRecentStatus(requestId);

                //check if request has no update yet (status = pending) -> a recently created request
                if (lsStatusRequest.isEmpty()) {
                    //Request newCreatedRequest = requestRepository.getOne(requestId);
                    Request newCreatedRequest = requestService.getRequest(requestId);
                    Optional<User> newCreatedRequestUser = userRepository.findById(newCreatedRequest.getUserID());
                    Town newCreatedRequestTown = townRepository.getOne(newCreatedRequest.getTownCode());
                    District newCreatedRequestDistrict = districtRepository.getOne(newCreatedRequest.getDistrictCode());
                    detailRequestModel.setRequestID(requestId); //requestID
                    detailRequestModel.setCustomerID(String.valueOf(newCreatedRequest.getUserID())); //customerID
                    detailRequestModel.setCustomerName(newCreatedRequestUser.get().getName()); //customerName
                    detailRequestModel.setCustomerPhoneNumber(newCreatedRequestUser.get().getPhoneNumber());//customerPhoneNumber
                    detailRequestModel.setCustomerDOB(newCreatedRequestUser.get().getDob()); //customerDOB
                    detailRequestModel.setRequestAddress(newCreatedRequest.getAddress()); //customer  address
                    detailRequestModel.setRequestDistrictID(newCreatedRequest.getDistrictCode());//district code
                    detailRequestModel.setRequestDistrictName(newCreatedRequestDistrict.getDistrictName());//district name
                    detailRequestModel.setRequestTownID(newCreatedRequest.getTownCode());//town code
                    detailRequestModel.setRequestTownName(newCreatedRequestTown.getTownName());//town name
                    detailRequestModel.setRequestMeetingTime(newCreatedRequest.getMeetingTime()); //meeting time
                    //=====================//
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdf2.format(newCreatedRequest.getCreatedTime());
                    String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//
                    detailRequestModel.setRequestCreatedTime(createdTime); //created time
                    detailRequestModel.setNurseID("Chưa có y tá nhận!");
                    detailRequestModel.setNurseName("Chưa có y tá nhận!");
                    detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                    detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                    detailRequestModel.setRequestStatus("pending"); //status
                    //set list selected test
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(requestId);
                    List<String> lsTestID = new ArrayList<>();
                    List<Integer> lsVersion = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        System.out.println(tracking.getTestID());
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                        lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                    }
                    System.out.println("Version Test" + lsVersion);

                    //set version
                    if (lsVersion.isEmpty()) {
                        detailRequestModel.setVersionOfTest(0);
                    } else {
                        detailRequestModel.setVersionOfTest(lsVersion.get(0));
                    }
                    //set list selected test
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote("Yêu cầu xét nghiệm mới tạo.");

                } else {
                    //Get the latest status of request
                    RequestHistory requestHistory = lsStatusRequest.get(0);

                    //get the latest status which status = accepted -> find nurse
                    List<RequestHistory> getListRequestAcceptedNurse =
                            requestHistoryRepository.findByRequestIDAndStatusOrderByCreatedTimeDesc(requestHistory.getRequestID(), "accepted");
                    if (getListRequestAcceptedNurse.isEmpty() || requestHistory.getStatus().equals("pending")) {
                        detailRequestModel.setNurseID("Chưa có y tá nhận!");
                        detailRequestModel.setNurseName("Chưa có y tá nhận!");
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
                        detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                        detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                    } else {
                        //get coordinator ID
                        detailRequestModel.setCoordinatorID(String.valueOf(getListRequestAcceptedCoordinator.get(0).getUserID()));
                        //get coordinator name
                        detailRequestModel.setCoordinatorName(userRepository.findById(getListRequestAcceptedCoordinator.get(0).getUserID()).get().getName());
                    }

                    //Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());
                    Request nowRequest = requestService.getRequest(requestHistory.getRequestID());
                    //return detail request
                    detailRequestModel.setRequestStatus(requestHistory.getStatus());
                    detailRequestModel.setRequestID(nowRequest.getRequestID());
                    detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
                    detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                    detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                    detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                    detailRequestModel.setRequestAddress(nowRequest.getAddress());
                    detailRequestModel.setRequestDistrictID(nowRequest.getDistrictCode());
                    detailRequestModel.setRequestDistrictName(districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                    detailRequestModel.setRequestTownID(nowRequest.getTownCode());
                    detailRequestModel.setRequestTownName(townRepository.findById(nowRequest.getTownCode()).get().getTownName());
                    detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
                    //=====================//
                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdf3.format(nowRequest.getCreatedTime());
                    String createdTime3 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//
                    detailRequestModel.setRequestCreatedTime(createdTime3);
                    // get list test
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                    List<String> lsTestID = new ArrayList<>();
                    List<Integer> lsVersion = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        System.out.println(tracking.getTestID());
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                        lsTestID.add(testID);
                    }
                    System.out.println("Test version " + lsVersion);

                    //set version
                    if (lsVersion.isEmpty()) {
                        detailRequestModel.setVersionOfTest(0);
                    } else {
                        detailRequestModel.setVersionOfTest(lsVersion.get(0));
                    }
                    //set list selected test
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote(requestHistory.getNote());
                }
                //===========================================
                lsDRequestDetail.add(detailRequestModel);
            }
            return new ResponseEntity<>(lsDRequestDetail, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý! Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
