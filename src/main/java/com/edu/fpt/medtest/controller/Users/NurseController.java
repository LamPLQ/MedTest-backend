package com.edu.fpt.medtest.controller.Users;


import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.*;
import com.edu.fpt.medtest.repository.*;
import com.edu.fpt.medtest.security.SecurityUtils;
import com.edu.fpt.medtest.service.Request.RequestHistoryService;
import com.edu.fpt.medtest.service.Request.RequestService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.ComfirmResponse;
import com.edu.fpt.medtest.utils.Validate;
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

    //Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel loginUser) {
        try {
            try {
                if (!loginUser.getRole().equals("NURSE")) {
                    return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại không được đăng nhập vào hệ thống!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được người dùng!"), HttpStatus.OK);
            }
            try {
                if (!Validate.isPhoneNumber(loginUser.getPhoneNumber())) {
                    return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                //null pointer exception
                //ex.printStackTrace();
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
            }
            try {
                if (loginUser.getPassword().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu không được để trống!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu không được để trống!"), HttpStatus.OK);
            }
            if (loginUser.getPassword().length() < 6) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải có nhiều hơn 6 kí tự"), HttpStatus.OK);
            }

            boolean existByPhoneNumberAndRole = userRepository.existsByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
            if (!existByPhoneNumberAndRole == true) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            User userLogin = userRepository.getUserByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
            //check active
            if (userLogin.getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
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
            //LoginAccountModel loginAccountModel = new LoginAccountModel();
            //loginAccountModel.setUserInfo(successfulUser);
            //loginAccountModel.setToken(token);
            //
            //new return
            ReturnLoginModel returnLoginModel = new ReturnLoginModel();
            returnLoginModel.setId(successfulUser.getId());
            returnLoginModel.setPhoneNumber(successfulUser.getPhoneNumber());
            returnLoginModel.setName(successfulUser.getName());
            returnLoginModel.setDob(successfulUser.getDob());
            returnLoginModel.setAddress(successfulUser.getAddress());
            returnLoginModel.setPassword(successfulUser.getPassword());
            returnLoginModel.setActive(successfulUser.getActive());
            returnLoginModel.setEmail(successfulUser.getEmail());
            returnLoginModel.setRole(successfulUser.getRole());
            returnLoginModel.setGender(successfulUser.getGender());
            returnLoginModel.setImage(successfulUser.getImage());
            returnLoginModel.setTownCode(successfulUser.getTownCode());
            returnLoginModel.setDistrictCode(successfulUser.getDistrictCode());
            //returnLoginModel.setToken(token);
            //System.out.println(returnLoginModel.getDistrictCode());
            if(successfulUser.getDistrictCode()==null && successfulUser.getTownCode()==null){
                returnLoginModel.setDistrictName("Chọn quận/huyện");
                returnLoginModel.setTownName("Chọn phường/xã");
            }else {
                returnLoginModel.setTownName(townRepository.findById(successfulUser.getTownCode()).get().getTownName());
                returnLoginModel.setDistrictName(districtRepository.findById(successfulUser.getDistrictCode()).get().getDistrictName());
            }

            LoginAccountModel loginAccountModel = new LoginAccountModel();
            loginAccountModel.setUserInfo(returnLoginModel);
            loginAccountModel.setToken(token);
            //
            return new ResponseEntity<>(loginAccountModel, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //nurse register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User nurse) {
        try {
            boolean existByPhoneAndRole = userRepository.existsByPhoneNumberAndRole(nurse.getPhoneNumber(), "NURSE");
            if (existByPhoneAndRole == true) {
                return new ResponseEntity<>(new ApiResponse(true, "Số điện thoại đã tồn tại!"), HttpStatus.OK);
            }
            nurse.setActive(1);
            nurse.setAddress(null);
            nurse.setRole("NURSE");
            nurse.setImage(nurse.getImage());
            nurse.setTownCode(null);
            nurse.setDistrictCode(null);
            nurse.setPassword(bCryptPasswordEncoder.encode(nurse.getPassword()));
            userService.saveUser(nurse);
            return new ResponseEntity<>(new ApiResponse(true, "Đã đăng kí thành công!"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list all nurse
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            List<User> users = userRepository.findAllByRole("NURSE");
            if (users.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get nurse - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        try {
            Optional<User> getNurse = userRepository.getUserByIdAndRole(id, "NURSE");
            if (!getNurse.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            User successfulUser = getNurse.get();
            ReturnLoginModel returnLoginModel = new ReturnLoginModel();
            returnLoginModel.setId(successfulUser.getId());
            returnLoginModel.setPhoneNumber(successfulUser.getPhoneNumber());
            returnLoginModel.setName(successfulUser.getName());
            returnLoginModel.setDob(successfulUser.getDob());
            returnLoginModel.setAddress(successfulUser.getAddress());
            returnLoginModel.setPassword(successfulUser.getPassword());
            returnLoginModel.setActive(successfulUser.getActive());
            returnLoginModel.setEmail(successfulUser.getEmail());
            returnLoginModel.setRole(successfulUser.getRole());
            returnLoginModel.setGender(successfulUser.getGender());
            returnLoginModel.setImage(successfulUser.getImage());
            returnLoginModel.setTownCode(successfulUser.getTownCode());
            returnLoginModel.setDistrictCode(successfulUser.getDistrictCode());
            //returnLoginModel.setToken(token);
            //System.out.println(returnLoginModel.getDistrictCode());
            if(successfulUser.getDistrictCode()==null && successfulUser.getTownCode()==null){
                returnLoginModel.setDistrictName("Chọn quận/huyện");
                returnLoginModel.setTownName("Chọn phường/xã");
            }else {
                returnLoginModel.setTownName(townRepository.findById(successfulUser.getTownCode()).get().getTownName());
                returnLoginModel.setDistrictName(districtRepository.findById(successfulUser.getDistrictCode()).get().getDistrictName());
            }
            return new ResponseEntity<>(returnLoginModel, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update nurse info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User nurse, @PathVariable("id") int id) {
        try {
            Optional<User> getNurse = userRepository.getUserByIdAndRole(id, "NURSE");
            if (!getNurse.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            if (getNurse.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            nurse.setId(id);
            userService.update(nurse);
            return new ResponseEntity<>(getNurse, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) {
        try {
            try {
                if (changePasswordModel.getNewPassword().isEmpty() || changePasswordModel.getOldPassword().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu không được để trống!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu không được để trống!"), HttpStatus.OK);
            }
            if (changePasswordModel.getOldPassword().length() < 6 || changePasswordModel.getNewPassword().length() < 6) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải nhiều hơn 6 kí tự"), HttpStatus.OK);
            }
            Optional<User> getNurse = userRepository.getUserByIdAndRole(id, "NURSE");
            if (!getNurse.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            if (getNurse.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getNurse.get().getPassword())) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu hiện tại không đúng!"), HttpStatus.OK);
            }
            if (changePasswordModel.getOldPassword().equals(changePasswordModel.getNewPassword())) {
                return new ResponseEntity<>(new ComfirmResponse(false, "Mật khẩu mới phải khác mật khẩu cũ", false), HttpStatus.OK);
            }
            changePasswordModel.setID(id);
            getNurse.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
            userService.saveUser(getNurse.get());
            return new ResponseEntity<>(new ApiResponse(true, "Thay đổi mật khẩu thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //Screen "Tìm đơn xét nghiệm"
    //List status = pending + coordinatorlostsample
    @GetMapping("/find-request")
    public ResponseEntity<?> findRequest() {
        try {
            // list request will return for nurse
            List<DetailRequestModel> lsFindingRequest = new ArrayList<>();

            //List all created request
            List<Request> lsAllRequest = requestService.lsRequestByCreatedTimeDesc();
            if (lsAllRequest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Hiện tại không có đơn đang chờ lấy mẫu!"), HttpStatus.OK);
            }
            //with each request in list request
            for (Request requestPending : lsAllRequest.subList(0, lsAllRequest.size() - 1)) {
                DetailRequestModel detailRequestModel = new DetailRequestModel();
                String requestId = requestPending.getRequestID();

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
                    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdf4.format(newCreatedRequest.getCreatedTime());
                    String createdTime4 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//
                    detailRequestModel.setRequestCreatedTime(createdTime4); //created time
                    detailRequestModel.setRequestUpdatedTime(createdTime4);
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
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                        lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                    }
                    System.out.println("Test version " + lsVersion);

                    //test version
                    if (lsVersion.isEmpty()) {
                        detailRequestModel.setVersionOfTest(0);
                    } else {
                        detailRequestModel.setVersionOfTest(lsVersion.get(0));
                    }
                    //list selected test
                    detailRequestModel.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detailRequestModel.setRequestAmount(String.valueOf(testAmount));
                    //set note
                    detailRequestModel.setRequestNote("Yêu cầu xét nghiệm mới tạo.");
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
                        detailRequestModel.setNurseID("Chưa có y tá nhận!");
                        //get nurse name
                        //detailRequestModel.setNurseName(userRepository.findById(requestHistory.getUserID()).get().getName());
                        detailRequestModel.setNurseName("Chưa có y tá nhận!");
                        detailRequestModel.setCoordinatorID("Chưa có điều phối viên xử lý!");
                        detailRequestModel.setCoordinatorName("Chưa có điều phối viên xử lý!");
                        //=====================//
                        SimpleDateFormat sdf59 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest59 = sdf59.format(requestHistory.getCreatedTime());
                        String createdTime59 = displayCreatedTest59.substring(0, 10) + "T" + displayCreatedTest59.substring(11) + ".000+0000";
                        //=====================//
                        detailRequestModel.setRequestUpdatedTime(createdTime59);

                        //Request nowRequest = requestRepository.getOne(requestHistory.getRequestID());
                        Request nowRequest = requestService.getRequest(requestHistory.getRequestID());
                        //return detail request
                        detailRequestModel.setRequestStatus(requestHistory.getStatus());
                        detailRequestModel.setRequestID(nowRequest.getRequestID());
                        detailRequestModel.setCustomerID(String.valueOf(nowRequest.getUserID()));
                        detailRequestModel.setCustomerName(userRepository.findById(nowRequest.getUserID()).get().getName());
                        detailRequestModel.setCustomerPhoneNumber(userRepository.findById(nowRequest.getUserID()).get().getPhoneNumber());
                        detailRequestModel.setCustomerDOB(userRepository.findById(nowRequest.getUserID()).get().getDob());
                        //detailRequestModel.setRequestAddress(nowRequest.getAddress() + " " + townRepository.findById(nowRequest.getTownCode()).get().getTownName() + " " + districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                        detailRequestModel.setRequestAddress(nowRequest.getAddress());
                        detailRequestModel.setRequestDistrictID(nowRequest.getDistrictCode());
                        detailRequestModel.setRequestDistrictName(districtRepository.findById(nowRequest.getDistrictCode()).get().getDistrictName());
                        detailRequestModel.setRequestTownID(nowRequest.getTownCode());
                        detailRequestModel.setRequestTownName(townRepository.findById(nowRequest.getTownCode()).get().getTownName());
                        detailRequestModel.setRequestMeetingTime(nowRequest.getMeetingTime());
                        //=====================//
                        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest = sdf5.format(nowRequest.getCreatedTime());
                        String createdTime5 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                        //=====================//
                        detailRequestModel.setRequestCreatedTime(createdTime5);
                        // get list test
                        List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                        List<String> lsTestID = new ArrayList<>();
                        List<Integer> lsVersion = new ArrayList<>();
                        long testAmount = 0;
                        for (RequestTest tracking : lsRequestTests) {
                            String testID = String.valueOf(tracking.getTestID());
                            testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                            lsTestID.add(testID);
                            lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                        }
                        System.out.println("lsTestVersion" + lsVersion);

                        //test version
                        if (lsVersion.isEmpty()) {
                            detailRequestModel.setVersionOfTest(0);
                        } else {
                            detailRequestModel.setVersionOfTest(lsVersion.get(0));
                        }

                        //list seletect test
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
                        detailRequestModel.setCoordinatorName(userRepository.findById(requestHistory.getUserID()).get().getName());
                        detailRequestModel.setNurseID("Chưa có y tá nhận!");
                        detailRequestModel.setNurseName("Chưa có y tá nhận!");
                        //=====================//
                        SimpleDateFormat sd60 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest60 = sd60.format(requestHistory.getCreatedTime());
                        String createdTime60 = displayCreatedTest60.substring(0, 10) + "T" + displayCreatedTest60.substring(11) + ".000+0000";
                        //=====================//
                        detailRequestModel.setRequestUpdatedTime(createdTime60);

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
                        SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest = sdf6.format(nowRequest.getCreatedTime());
                        String createdTime6 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                        //=====================//

                        detailRequestModel.setRequestCreatedTime(createdTime6);
                        // get list test
                        List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(nowRequest.getRequestID());
                        List<String> lsTestID = new ArrayList<>();
                        List<Integer> lsVersionTest = new ArrayList<>();
                        long testAmount = 0;
                        for (RequestTest tracking : lsRequestTests) {
                            String testID = String.valueOf(tracking.getTestID());
                            testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                            lsTestID.add(testID);
                            lsVersionTest.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                        }
                        System.out.println("List test version " + lsVersionTest);
                        //verrsion
                        if (lsVersionTest.isEmpty()) {
                            detailRequestModel.setVersionOfTest(0);
                        } else {
                            detailRequestModel.setVersionOfTest(lsVersionTest.get(0));
                        }
                        //list selected test
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
                return new ResponseEntity<>(new ApiResponse(false, "Tất cả các đơn đều đã được nhận!"), HttpStatus.OK);
            return new ResponseEntity<>(lsFindingRequest, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //Screen "Đơn đang nhận"
    //status = {accepted, lostsample, transporting, reaccepted, retransporting, relostsample }
    @GetMapping("{id}/list/handling")
    public ResponseEntity<?> lsHandling(@PathVariable("id") int nurseID) {
        try {
            Optional<User> processingUser = userRepository.getUserByIdAndRole(nurseID, "NURSE");
            if (!processingUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại"), HttpStatus.OK);
            }
            if (processingUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            //list handling return
            List<DetailRequestModel> lsNurseHandling = new ArrayList<>();

            //List all created request
            //List<Request> lsAllRequest = requestService.lsRequest();
            List<Request> lsAllRequest = requestService.lsRequestByMeetingTime();
            if (lsAllRequest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Hiện tại không có đơn đang chờ lấy mẫu!"), HttpStatus.OK);
            }
            //with each request in list request
            List<RequestHistory> lsLastStatus = new ArrayList<>();
            for (Request requestPending : lsAllRequest.subList(1, lsAllRequest.size())) {
                boolean existRequestID = requestHistoryRepository.existsByRequestID(requestPending.getRequestID());
                if (existRequestID == false) {
                    System.out.println("Don't have this requestID");
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
                        (request.getUserID() == nurseID && (request.getStatus().equals("transporting"))) ||
                        (request.getUserID() == nurseID && (request.getStatus().equals("reaccepted"))) ||       //đã nhận đơn bị mất do điều phối viên
                        (request.getUserID() == nurseID && (request.getStatus().equals("retransporting"))) ||   //đang vận chuyển đơn bị mất do điều phối viên
                        (request.getUserID() == nurseID && (request.getStatus().equals("relostsample")))) {    //đang chờ y tá lấy lại mẫu của đơn bị mất do điều phối viên
                    DetailRequestModel detail = new DetailRequestModel();
                    //requestID
                    detail.setRequestID(request.getRequestID());
                    //nurseID
                    detail.setNurseID(String.valueOf(request.getUserID()));
                    //nurseName
                    detail.setNurseName(userRepository.findById(request.getUserID()).get().getName());
                    //request of request history
                    //Request recentRequest = requestRepository.getOne(request.getRequestID());
                    Request recentRequest = requestService.getRequest(request.getRequestID());
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
                    detail.setRequestAddress(recentRequest.getAddress());
                    //+ " " +
                    //townRepository.findById(recentRequest.getTownCode()).get().getTownName() + " " +
                    //districtRepository.findById(recentRequest.getDistrictCode()).get().getDistrictName());
                    detail.setRequestTownID(recentRequest.getTownCode());
                    detail.setRequestTownName(townRepository.findById(recentRequest.getTownCode()).get().getTownName());
                    detail.setRequestDistrictID(recentRequest.getDistrictCode());
                    detail.setRequestDistrictName(districtRepository.findById(recentRequest.getDistrictCode()).get().getDistrictName());
                    //request meetingTime
                    detail.setRequestMeetingTime(recentRequest.getMeetingTime());
                    //request status
                    detail.setRequestStatus(request.getStatus());
                    //request created time
                    //=====================//
                    SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest = sdf8.format(recentRequest.getCreatedTime());
                    String createdTime8 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                    //=====================//
                    detail.setRequestCreatedTime(createdTime8);
                    //=====================//
                    SimpleDateFormat sdf80 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String displayCreatedTest80 = sdf80.format(request.getCreatedTime());
                    String createdTime80 = displayCreatedTest80.substring(0, 10) + "T" + displayCreatedTest80.substring(11) + ".000+0000";
                    //=====================//
                    detail.setRequestUpdatedTime(createdTime80);
                    //coordinator
                    List<RequestHistory> requestLostByCoordinator = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(), "coordinatorlostsample");
                    if (requestLostByCoordinator.isEmpty()) {
                        detail.setCoordinatorID("Chưa có điều phối viên xử lý!");
                        detail.setCoordinatorName("Chưa có điều phối viên xử lý!");
                    } else {
                        detail.setCoordinatorID(String.valueOf(requestLostByCoordinator.get(0).getUserID()));
                        detail.setCoordinatorName(userRepository.findById(requestLostByCoordinator.get(0).getUserID()).get().getName());
                    }
                    //requestNote
                    detail.setRequestNote(request.getNote());
                    List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(recentRequest.getRequestID());
                    List<String> lsTestID = new ArrayList<>();
                    List<Integer> lsVersion = new ArrayList<>();
                    long testAmount = 0;
                    for (RequestTest tracking : lsRequestTests) {
                        String testID = String.valueOf(tracking.getTestID());
                        testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                        lsTestID.add(testID);
                        lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                    }
                    if (lsVersion.isEmpty()) {
                        detail.setVersionOfTest(0);
                    } else {
                        detail.setVersionOfTest(lsVersion.get(0));
                    }
                    detail.setLsSelectedTest(lsTestID);
                    //set amount of test
                    detail.setRequestAmount(String.valueOf(testAmount));
                    lsNurseHandling.add(detail);
                }
            }
            if (lsNurseHandling.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Y tá không còn đơn cần xử lý!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsNurseHandling, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //Screen "Lịch sử nhận đơn"
    //status = {closed, waitingforresult, coordinatorlostsample }
    @GetMapping("{id}/list/request-completed")
    public ResponseEntity<?> lsCompleted(@PathVariable("id") int nurseID) {
        try {
            Optional<User> processingUser = userRepository.getUserByIdAndRole(nurseID, "NURSE");
            if (!processingUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại"), HttpStatus.OK);
            }
            if (processingUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            //list completed
            List<CompletedRequestModel> lsCompletedReqs = new ArrayList<>();

            //List all created request
            List<Request> lsAllRequest = requestService.lsRequest();
            if (lsAllRequest.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Hiện tại không có đơn đang chờ lấy mẫu!"), HttpStatus.OK);
            }
            //with each request in list request
            List<RequestHistory> lsLastStatus = new ArrayList<>();
            for (Request requestPending : lsAllRequest.subList(1, lsAllRequest.size())) {
                boolean existRequestID = requestHistoryRepository.existsByRequestID(requestPending.getRequestID());
                if (existRequestID == false) {
                    //System.out.println(requestPending + "Don't have this requestID");
                } else {
                    //System.out.println(requestPending.getRequestID() + "-OK");
                    RequestHistory requestAvailable = requestHistoryRepository.findByRequestIDOrderByCreatedTimeDesc(requestPending.getRequestID()).get(0);
                    //System.out.println(requestAvailable.getNote());
                    lsLastStatus.add(requestAvailable);
                }
            }
            for (RequestHistory request : lsLastStatus) {
                if (request.getStatus().equals("closed") ||
                        request.getStatus().equals("waitingforresult") ||
                        request.getStatus().equals("coordinatorlostsample") ||
                        request.getStatus().equals("canceled")) {
                    //list of "accepted" status with each request history
                    boolean existByRequestIDAndStatusAccepted = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "accepted", nurseID);
                    boolean existByRequestIDAndStatusPending = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "pending", nurseID);
                    //System.out.println("Test" + request.getRequestID());
                    //System.out.println("Test" + nurseID);
                    if (existByRequestIDAndStatusAccepted == false || existByRequestIDAndStatusPending==true) {
                        System.out.println("No request history with /accepted/ status");
                    } else {
                        RequestHistory acceptedStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "accepted", nurseID).get(0);
                        //System.out.println(acceptedStatusRequest.getRequestID() + acceptedStatusRequest.getNote());

                        boolean existByRequestIDAndStatusTransporting = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "transporting", nurseID);
                        //boolean existByRequestIDAndStatusReTransporting = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "retransporting", nurseID);
                        if (existByRequestIDAndStatusTransporting == false) {
                            System.out.println("No request history with /transporting/ status");
                            //customer cancel when nurse accepted request
                            CompletedRequestModel cancelAfterAccept = new CompletedRequestModel();
                            cancelAfterAccept.setRequestID(request.getRequestID());
                            ////////////////////Object Request
                            //Request cancelAfterAcceptRequest = requestRepository.getOne(request.getRequestID());
                            Request cancelAfterAcceptRequest = requestService.getRequest(request.getRequestID());
                            ////////////////////
                            cancelAfterAccept.setCustomerID(String.valueOf(cancelAfterAcceptRequest.getUserID()));
                            cancelAfterAccept.setCustomerName(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getName());
                            cancelAfterAccept.setCustomerPhoneNumber(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getPhoneNumber());
                            cancelAfterAccept.setCustomerDOB(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getDob());
                            cancelAfterAccept.setRequestAddress(cancelAfterAcceptRequest.getAddress());
                            cancelAfterAccept.setRequestDistrictID(cancelAfterAcceptRequest.getDistrictCode());
                            cancelAfterAccept.setRequestDistrictName(districtRepository.findById(cancelAfterAcceptRequest.getDistrictCode()).get().getDistrictName());
                            cancelAfterAccept.setRequestTownID(cancelAfterAcceptRequest.getTownCode());
                            cancelAfterAccept.setRequestTownName(townRepository.findById(cancelAfterAcceptRequest.getTownCode()).get().getTownName());
                            cancelAfterAccept.setRequestMeetingTime(cancelAfterAcceptRequest.getMeetingTime());
                            //=====================//
                            SimpleDateFormat sdf9 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest = sdf9.format(cancelAfterAcceptRequest.getCreatedTime());
                            String createdTime9 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestCreatedTime(createdTime9);
                            //=====================//
                            SimpleDateFormat sdf90 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest90 = sdf90.format(request.getCreatedTime());
                            String createdTime90 = displayCreatedTest90.substring(0, 10) + "T" + displayCreatedTest90.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestUpdatedTime(createdTime90);
                            cancelAfterAccept.setNurseID(String.valueOf(nurseID));
                            cancelAfterAccept.setNurseName(userRepository.findById(nurseID).get().getName());
                            List<RequestHistory> lsStatusAferCancelWaiting = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(), "waitingforresult");
                            List<RequestHistory> lsStatusAfterCancelClosed = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(), "closed");
                            List<RequestHistory> lsStatusAfterCancelLostSample = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(), "coordinatorlostsample");
                            if (lsStatusAfterCancelLostSample.isEmpty() && lsStatusAfterCancelClosed.isEmpty() && lsStatusAferCancelWaiting.isEmpty()) {
                                cancelAfterAccept.setCoordinatorID("Chưa có điều phối viên nhận");
                                cancelAfterAccept.setCoordinatorName("Chưa có điều phối viên nhận");
                            } else {
                                cancelAfterAccept.setCoordinatorID("Điều phối viên đã tiếp nhận đơn.");
                                cancelAfterAccept.setCoordinatorName("Điều phối viên đã tiếp nhận đơn.");
                            }
                            cancelAfterAccept.setRequestStatus(request.getStatus());
                            cancelAfterAccept.setRequestNote(request.getNote());
                            //=====================//
                            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest4 = sdf4.format(acceptedStatusRequest.getCreatedTime());
                            String createdTime4 = displayCreatedTest4.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestAcceptedTime(createdTime4);
                            List<RequestTest> lsRequestTests1 = requestTestRepository.getAllByRequestID(cancelAfterAcceptRequest.getRequestID());
                            List<String> lsTestID1 = new ArrayList<>();
                            List<Integer> lsVersion = new ArrayList<>();
                            long testAmount1 = 0;
                            for (RequestTest tracking : lsRequestTests1) {
                                String testID = String.valueOf(tracking.getTestID());
                                testAmount1 += testRepository.findById(tracking.getTestID()).get().getPrice();
                                lsTestID1.add(testID);
                                lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                            }
                            //version
                            if (lsVersion.isEmpty()) {
                                cancelAfterAccept.setVersionOfTest(0);
                            } else {
                                cancelAfterAccept.setVersionOfTest(lsVersion.get(0));
                            }
                            //list selected test
                            cancelAfterAccept.setLsSelectedTest(lsTestID1);
                            //set amount of test
                            cancelAfterAccept.setRequestAmount(String.valueOf(testAmount1));
                            lsCompletedReqs.add(cancelAfterAccept);
                            //////////////////////////////////////////////
                        } else {
                            RequestHistory transportingStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "transporting", nurseID).get(0);
                            CompletedRequestModel completedRequestModel = new CompletedRequestModel();
                            completedRequestModel.setRequestID(request.getRequestID());
                            ////////////////////Object Request
                            Request workingRequest = requestService.getRequest(request.getRequestID());
                            ////////////////////
                            completedRequestModel.setCustomerID(String.valueOf(workingRequest.getUserID()));
                            completedRequestModel.setCustomerName(userRepository.findById(workingRequest.getUserID()).get().getName());
                            completedRequestModel.setCustomerPhoneNumber(userRepository.findById(workingRequest.getUserID()).get().getPhoneNumber());
                            completedRequestModel.setCustomerDOB(userRepository.findById(workingRequest.getUserID()).get().getDob());
                            completedRequestModel.setRequestAddress(workingRequest.getAddress());
                            completedRequestModel.setRequestDistrictID(workingRequest.getDistrictCode());
                            completedRequestModel.setRequestDistrictName(districtRepository.findById(workingRequest.getDistrictCode()).get().getDistrictName());
                            completedRequestModel.setRequestTownID(workingRequest.getTownCode());
                            completedRequestModel.setRequestTownName(townRepository.findById(workingRequest.getTownCode()).get().getTownName());
                            completedRequestModel.setRequestMeetingTime(workingRequest.getMeetingTime());
                            //=====================//
                            SimpleDateFormat sdf10 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest = sdf10.format(workingRequest.getCreatedTime());
                            String createdTime10 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            completedRequestModel.setRequestCreatedTime(createdTime10);
                            //=====================//
                            SimpleDateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest11 = sdf11.format(request.getCreatedTime());
                            String createdTime11 = displayCreatedTest11.substring(0, 10) + "T" + displayCreatedTest11.substring(11) + ".000+0000";
                            //=====================//
                            completedRequestModel.setRequestUpdatedTime(createdTime11);
                            completedRequestModel.setNurseID(String.valueOf(nurseID));
                            completedRequestModel.setNurseName(userRepository.findById(nurseID).get().getName());
                            completedRequestModel.setCoordinatorID(String.valueOf(request.getUserID()));
                            completedRequestModel.setCoordinatorName(userRepository.findById(request.getUserID()).get().getName());
                            completedRequestModel.setRequestStatus(request.getStatus());
                            completedRequestModel.setRequestNote(request.getNote());
                            //=====================//
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest2 = sdf2.format(acceptedStatusRequest.getCreatedTime());
                            String createdTime2 = displayCreatedTest2.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            completedRequestModel.setRequestAcceptedTime(createdTime2);
                            //=====================//
                            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest3 = sdf3.format(transportingStatusRequest.getCreatedTime());
                            String createdTime3 = displayCreatedTest3.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            completedRequestModel.setRequestTransportingTime(createdTime3);
                            List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(workingRequest.getRequestID());
                            List<String> lsTestID = new ArrayList<>();
                            List<Integer> lsVersion = new ArrayList<>();
                            long testAmount = 0;
                            for (RequestTest tracking : lsRequestTests) {
                                String testID = String.valueOf(tracking.getTestID());
                                testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                                lsTestID.add(testID);
                                lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                            }
                            //version
                            if (lsVersion.isEmpty()) {
                                completedRequestModel.setVersionOfTest(0);
                            } else {
                                completedRequestModel.setVersionOfTest(lsVersion.get(0));
                            }
                            //list handling
                            completedRequestModel.setLsSelectedTest(lsTestID);
                            //set amount of test
                            completedRequestModel.setRequestAmount(String.valueOf(testAmount));
                            lsCompletedReqs.add(completedRequestModel);
                        }
                    }
                    /////////////////////////////////
                    boolean existByRequestIDAndStatusReAccepted = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "reaccepted", nurseID);
                    if (existByRequestIDAndStatusReAccepted == false) {
                        System.out.println("No request history with /reaccepted/ status");
                    } else {
                        RequestHistory acceptedStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "reaccepted", nurseID).get(0);
                        //System.out.println(acceptedStatusRequest.getRequestID() + acceptedStatusRequest.getNote());

                        //boolean existByRequestIDAndStatusTransporting = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "transporting", nurseID);
                        /*boolean existByRequestIDAndStatusReTransporting = requestHistoryRepository.existsByRequestIDAndStatusAndUserID(request.getRequestID(), "retransporting", nurseID);
                        if (existByRequestIDAndStatusReTransporting == false) {
                            System.out.println("No request history with /transporting/ status");
                            //customer cancel when nurse accepted request
                            CompletedRequestModel cancelAfterAccept = new CompletedRequestModel();
                            cancelAfterAccept.setRequestID(request.getRequestID());
                            ////////////////////Object Request
                            //Request cancelAfterAcceptRequest = requestRepository.getOne(request.getRequestID());
                            Request cancelAfterAcceptRequest = requestService.getRequest(request.getRequestID());
                            ////////////////////
                            cancelAfterAccept.setCustomerID(String.valueOf(cancelAfterAcceptRequest.getUserID()));
                            cancelAfterAccept.setCustomerName(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getName());
                            cancelAfterAccept.setCustomerPhoneNumber(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getPhoneNumber());
                            cancelAfterAccept.setCustomerDOB(userRepository.findById(cancelAfterAcceptRequest.getUserID()).get().getDob());
                            cancelAfterAccept.setRequestAddress(cancelAfterAcceptRequest.getAddress());
                            cancelAfterAccept.setRequestDistrictID(cancelAfterAcceptRequest.getDistrictCode());
                            cancelAfterAccept.setRequestDistrictName(districtRepository.findById(cancelAfterAcceptRequest.getDistrictCode()).get().getDistrictName());
                            cancelAfterAccept.setRequestTownID(cancelAfterAcceptRequest.getTownCode());
                            cancelAfterAccept.setRequestTownName(townRepository.findById(cancelAfterAcceptRequest.getTownCode()).get().getTownName());
                            cancelAfterAccept.setRequestMeetingTime(cancelAfterAcceptRequest.getMeetingTime());
                            //=====================//
                            SimpleDateFormat sdf9 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest = sdf9.format(cancelAfterAcceptRequest.getCreatedTime());
                            String createdTime9 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestCreatedTime(createdTime9);
                            //=====================//
                            SimpleDateFormat sdf90 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest90 = sdf90.format(request.getCreatedTime());
                            String createdTime90 = displayCreatedTest90.substring(0, 10) + "T" + displayCreatedTest90.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestUpdatedTime(createdTime90);


                            cancelAfterAccept.setNurseID(String.valueOf(nurseID));
                            cancelAfterAccept.setNurseName(userRepository.findById(nurseID).get().getName());
                            List<RequestHistory> lsStatusAferCancelWaiting = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(),"waitingforresult");
                            List<RequestHistory> lsStatusAfterCancelClosed = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(), "closed");
                            List<RequestHistory> lsStatusAfterCancelLostSample = requestHistoryRepository.findByRequestIDAndStatus(request.getRequestID(),"coordinatorlostsample");
                            if(lsStatusAfterCancelLostSample.isEmpty() && lsStatusAfterCancelClosed.isEmpty() && lsStatusAferCancelWaiting.isEmpty()){
                                cancelAfterAccept.setCoordinatorID("Chưa có điều phối viên nhận");
                                cancelAfterAccept.setCoordinatorName("Chưa có điều phối viên nhận");
                            }else{
                                cancelAfterAccept.setCoordinatorID("Điều phối viên đã tiếp nhận đơn.");
                                cancelAfterAccept.setCoordinatorName("Điều phối viên đã tiếp nhận đơn.");
                            }
                            cancelAfterAccept.setRequestStatus(request.getStatus());
                            cancelAfterAccept.setRequestNote(request.getNote());
                            //=====================//
                            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String displayCreatedTest4 = sdf4.format(acceptedStatusRequest.getCreatedTime());
                            String createdTime4 = displayCreatedTest4.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                            //=====================//
                            cancelAfterAccept.setRequestAcceptedTime(createdTime4);
                            List<RequestTest> lsRequestTests1 = requestTestRepository.getAllByRequestID(cancelAfterAcceptRequest.getRequestID());
                            List<String> lsTestID1 = new ArrayList<>();
                            List<Integer> lsVersion = new ArrayList<>();
                            long testAmount1 = 0;
                            for (RequestTest tracking : lsRequestTests1) {
                                String testID = String.valueOf(tracking.getTestID());
                                testAmount1 += testRepository.findById(tracking.getTestID()).get().getPrice();
                                lsTestID1.add(testID);
                                lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                            }
                            //version
                            if (lsVersion.isEmpty()) {
                                cancelAfterAccept.setVersionOfTest(0);
                            } else {
                                cancelAfterAccept.setVersionOfTest(lsVersion.get(0));
                            }
                            //list selected test
                            cancelAfterAccept.setLsSelectedTest(lsTestID1);
                            //set amount of test
                            cancelAfterAccept.setRequestAmount(String.valueOf(testAmount1));
                            lsCompletedReqs.add(cancelAfterAccept);
                            //////////////////////////////////////////////
                        } else {*/
                        RequestHistory transportingStatusRequest = requestHistoryRepository.findAllByRequestIDAndStatusAndUserIDOrderByCreatedTimeDesc(request.getRequestID(), "retransporting", nurseID).get(0);
                        CompletedRequestModel completedRequestModel = new CompletedRequestModel();
                        completedRequestModel.setRequestID(request.getRequestID());
                        ////////////////////Object Request
                        Request workingRequest = requestService.getRequest(request.getRequestID());
                        ////////////////////
                        completedRequestModel.setCustomerID(String.valueOf(workingRequest.getUserID()));
                        completedRequestModel.setCustomerName(userRepository.findById(workingRequest.getUserID()).get().getName());
                        completedRequestModel.setCustomerPhoneNumber(userRepository.findById(workingRequest.getUserID()).get().getPhoneNumber());
                        completedRequestModel.setCustomerDOB(userRepository.findById(workingRequest.getUserID()).get().getDob());
                        completedRequestModel.setRequestAddress(workingRequest.getAddress());
                        completedRequestModel.setRequestDistrictID(workingRequest.getDistrictCode());
                        completedRequestModel.setRequestDistrictName(districtRepository.findById(workingRequest.getDistrictCode()).get().getDistrictName());
                        completedRequestModel.setRequestTownID(workingRequest.getTownCode());
                        completedRequestModel.setRequestTownName(townRepository.findById(workingRequest.getTownCode()).get().getTownName());
                        completedRequestModel.setRequestMeetingTime(workingRequest.getMeetingTime());
                        //=====================//
                        SimpleDateFormat sdf10 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest = sdf10.format(workingRequest.getCreatedTime());
                        String createdTime10 = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                        //=====================//
                        completedRequestModel.setRequestCreatedTime(createdTime10);
                        //=====================//
                        SimpleDateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest11 = sdf11.format(request.getCreatedTime());
                        String createdTime11 = displayCreatedTest11.substring(0, 10) + "T" + displayCreatedTest11.substring(11) + ".000+0000";
                        //=====================//
                        completedRequestModel.setRequestUpdatedTime(createdTime11);
                        completedRequestModel.setNurseID(String.valueOf(nurseID));
                        completedRequestModel.setNurseName(userRepository.findById(nurseID).get().getName());
                        completedRequestModel.setCoordinatorID(String.valueOf(request.getUserID()));
                        completedRequestModel.setCoordinatorName(userRepository.findById(request.getUserID()).get().getName());
                        completedRequestModel.setRequestStatus(request.getStatus());
                        completedRequestModel.setRequestNote(request.getNote());
                        //=====================//
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest2 = sdf2.format(acceptedStatusRequest.getCreatedTime());
                        String createdTime2 = displayCreatedTest2.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                        //=====================//
                        completedRequestModel.setRequestAcceptedTime(createdTime2);
                        //=====================//
                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String displayCreatedTest3 = sdf3.format(transportingStatusRequest.getCreatedTime());
                        String createdTime3 = displayCreatedTest3.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                        //=====================//
                        completedRequestModel.setRequestTransportingTime(createdTime3);
                        List<RequestTest> lsRequestTests = requestTestRepository.getAllByRequestID(workingRequest.getRequestID());
                        List<String> lsTestID = new ArrayList<>();
                        List<Integer> lsVersion = new ArrayList<>();
                        long testAmount = 0;
                        for (RequestTest tracking : lsRequestTests) {
                            String testID = String.valueOf(tracking.getTestID());
                            testAmount += testRepository.findById(tracking.getTestID()).get().getPrice();
                            lsTestID.add(testID);
                            lsVersion.add(testRepository.getOne(tracking.getTestID()).getVersionID());
                        }
                        //version
                        if (lsVersion.isEmpty()) {
                            completedRequestModel.setVersionOfTest(0);
                        } else {
                            completedRequestModel.setVersionOfTest(lsVersion.get(0));
                        }
                        //list handling
                        completedRequestModel.setLsSelectedTest(lsTestID);
                        //set amount of test
                        completedRequestModel.setRequestAmount(String.valueOf(testAmount));
                        lsCompletedReqs.add(completedRequestModel);
                    }
                    //}
                    /////////////////////////////////
                }
            }
            if (lsCompletedReqs.isEmpty()) {
                return new ResponseEntity(new ApiResponse(false, "Y tá chưa hoàn thiện yêu cầu nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsCompletedReqs, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
