package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.*;
import com.edu.fpt.medtest.model.*;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TokenRepository;
import com.edu.fpt.medtest.repository.TownRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.security.SecurityUtils;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.service.SmsService.SmsService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private TownRepository townRepository;

    /*@Autowired
    private SentMailModel sentMailModel;

    @Autowired
    private MailService mailService;*/

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SmsService smsService;


    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CheckEmailExist checkEmailExist;

    // List user with state ACTIVE
    @GetMapping("/list/active")
    public ResponseEntity<?> listActive() {
        List<User> lsUsersActive = userService.lsUserActive();
        if (lsUsersActive.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở trạng thái đang hoạt động!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(lsUsersActive, HttpStatus.OK);
    }

    // List user with state DE-ACTIVE
    @GetMapping("/list/deactive")
    public ResponseEntity<?> listDeactive() {
        List<User> lsUsersDeactive = userService.lsUserNotActive();
        if (lsUsersDeactive.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở trạng thái khoá!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(lsUsersDeactive, HttpStatus.OK);
    }

    //List user in 1 district
    @GetMapping("/list/district/{code}")
    public ResponseEntity<?> listDistrict(@PathVariable("code") String code) {
        List<User> lsByDistrict = userService.lsUserByDistrict(code);
        Optional<District> district = districtRepository.findById(code);
        if (lsByDistrict.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng tại quận/huyện " + district.get().getDistrictName()), HttpStatus.OK);
        return new ResponseEntity<>(lsByDistrict, HttpStatus.OK);
    }

    //List user in 1 town
    @GetMapping("/list/town/{code}")
    public ResponseEntity<?> listTown(@PathVariable("code") String code) {
        List<User> lsByTown = userService.lsUserByTown(code);
        Optional<Town> town = townRepository.findById(code);
        if (lsByTown.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở phường/huyện " + town.get().getTownName()), HttpStatus.OK);
        return new ResponseEntity<>(lsByTown, HttpStatus.OK);
    }

    //Reset password for user
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@RequestBody UserProcessingModel userProcessingModel, @PathVariable("id") int id) {
        Optional<User> processingUser = userService.getUserByID(userProcessingModel.getUserProcessingID());
        if (!processingUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Không tồn tại tài khoản!"), HttpStatus.OK);
        }
        if (!processingUser.get().getRole().equals("ADMIN")) {
            return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại không được thực hiện được chức năng này!"), HttpStatus.OK);
        }
        Optional<User> userByID = userService.getUserByID(id);
        if (!userByID.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "Không tồn tại người dùng này!"), HttpStatus.OK);
        }
        /*if (userByID.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại không thực hiện được chức năng này!"), HttpStatus.OK);
        sentMailModel.setEmail(userByID.get().getEmail());
        sentMailModel.setPhoneNumber(userByID.get().getPhoneNumber());
        sentMailModel.setRole(userByID.get().getRole());
        try {
            mailService.sendEmail(sentMailModel);
        } catch (MailException mailException) {
            System.out.println(mailException);
        }*/
        SmsRequest smsRequest = new SmsRequest(userByID.get().getPhoneNumber(), userByID.get().getRole());
        smsService.resetPassword(smsRequest);
        return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu mới đã được gửi đến số điện thoại " + userByID.get().getPhoneNumber()), HttpStatus.OK);
    }

    //forgotPassword for customer
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel) {
        boolean existPhoneNumber = userRepository.existsByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
        if (!existPhoneNumber == true) {
            return new ResponseEntity<>(new ComfirmResponse(true, "Không tìm thấy số điện thoại đã nhập!", false), HttpStatus.OK);
        }
        User forgotPasswordUser = userService.getUserByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
        /*sentMailModel.setEmail(forgotPasswordUser.getEmail());
        sentMailModel.setPhoneNumber(forgotPasswordUser.getPhoneNumber());
        sentMailModel.setRole(forgotPasswordUser.getRole());
        try {
            mailService.sendEmail(sentMailModel);
        } catch (MailException mailException) {
            System.out.println(mailException);
        }*/
        SmsRequest smsRequest = new SmsRequest(forgotPasswordUser.getPhoneNumber(), forgotPasswordUser.getRole());
        smsService.resetPassword(smsRequest);
        return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu mới đã được gửi đến số điện thoại " + forgotPasswordUser.getPhoneNumber()), HttpStatus.OK);
    }

    //list notification for user
    @GetMapping("{id}/notifications/list")
    public ResponseEntity<?> getLsNotification(@PathVariable("id") int userID) {
        List<Notification> lsNoti = notificationService.lsNotification(userID);
        if (lsNoti.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Hiện tại không có thông báo nào!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(lsNoti, HttpStatus.OK);
    }

    //list all user
    @GetMapping("/list-all-user")
    public ResponseEntity listAllUser() {
        List<User> lsAllUser = userService.getListUser();
        if (lsAllUser.isEmpty()) {
            return new ResponseEntity(new ApiResponse(true, "Không có người dùng trong hệ thống!"), HttpStatus.OK);
        }
        List<User> returnList = new ArrayList<>();
        for (User user : lsAllUser.subList(1, lsAllUser.size())) {
            returnList.add(user);
        }
        if (returnList.isEmpty()) {
            return new ResponseEntity(new ApiResponse(true, "Không có người dùng!"), HttpStatus.OK);
        }
        return new ResponseEntity(returnList, HttpStatus.OK);
    }

    //send OTP to phone by messsing
    //      to verify phone is true
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendSmS(@Valid @RequestBody SmsRequest smsRequest) {
        boolean existByPhoneAndRole = userRepository.existsByPhoneNumberAndRole(smsRequest.getPhoneNumber(), smsRequest.getRole());
        if (existByPhoneAndRole == true) {
            return new ResponseEntity<>(new SendMessageResponse(true, "Số điện thoại đã tồn tại!", false), HttpStatus.OK);
        }
        List<ValidPhoneToken> lsAllTokenOfPhoneNumber = tokenRepository.getAllByPhoneNumber(smsRequest.getPhoneNumber());
        for (ValidPhoneToken validPhoneToken : lsAllTokenOfPhoneNumber) {
            tokenRepository.delete(validPhoneToken);
        }
        smsService.sendSms(smsRequest);
        return new ResponseEntity<>(new SendMessageResponse(true, "Gửi thành công tin nhắn đến số điện thoại.", true), HttpStatus.OK);
    }

    //check OPT is valid
    //              valid=true -> insert 1 object user -> message insert successful
    //              valid=false -> return message
    @PostMapping("/valid-phone-otp")
    public ResponseEntity<?> isValidPhoneNumberOTP(@RequestBody CheckOTPModel checkOTPModel) {
        Optional<ValidPhoneToken> checkValidPhoneToken = tokenRepository.getByPhoneNumberAndToken(checkOTPModel.getPhoneNumber(), checkOTPModel.getOtp());
        if (!checkValidPhoneToken.isPresent()) {
            return new ResponseEntity<>(new CheckOTPResponse(true, "Mã OTP không hợp lệ!", false, false), HttpStatus.OK);
        }
        if (checkValidPhoneToken.get().getExpiredTime() <= System.currentTimeMillis()) {
            tokenRepository.delete(tokenRepository.getOne(checkValidPhoneToken.get().getSessionID()));
            return new ResponseEntity<>(new CheckOTPResponse(true, "Mã OTP hết hạn!", false, true), HttpStatus.OK);
        }
        /*boolean emailExist = checkEmailExist.isAddressValid(checkOTPModel.getEmail());
        if(emailExist==false){
            return new ResponseEntity<>(new ApiResponse(false,"Email không tồn tại!"), HttpStatus.OK);
        }*/
        String enCryptPassword = bCryptPasswordEncoder.encode(checkOTPModel.getPassword());
        User registeredUser = new User();
        registeredUser.setName(checkOTPModel.getName());
        registeredUser.setPhoneNumber(checkOTPModel.getPhoneNumber());
        registeredUser.setEmail(checkOTPModel.getEmail());
        registeredUser.setDob(checkOTPModel.getDob());
        registeredUser.setGender(checkOTPModel.getGender());
        registeredUser.setPassword(checkOTPModel.getPassword());
        registeredUser.setActive(1);
        registeredUser.setAddress(null);
        registeredUser.setRole("CUSTOMER");
        registeredUser.setImage(registeredUser.getImage());
        registeredUser.setTownCode(null);
        registeredUser.setDistrictCode(null);
        registeredUser.setPassword(enCryptPassword);
        userService.saveUser(registeredUser);
        tokenRepository.delete(tokenRepository.getOne(checkValidPhoneToken.get().getSessionID()));
        return new ResponseEntity<>(new CheckOTPResponse(true, "Nhập mã OTP thành công", true, true), HttpStatus.OK);
    }

    //resendOTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody SmsRequest smsRequest) {
        Optional<ValidPhoneToken> validPhoneToken = tokenRepository.getByPhoneNumber(smsRequest.getPhoneNumber());
        if (!validPhoneToken.isPresent()) {
            return new ResponseEntity<>(new SendMessageResponse(true, "Số điện thoại không tồn tại!", false), HttpStatus.OK);
        }
        tokenRepository.delete(validPhoneToken.get());
        smsService.sendSms(smsRequest);
        return new ResponseEntity<>(new SendMessageResponse(true, "Đã gửi lại mã OTP tới đến số điện thoại.", true), HttpStatus.OK);
    }

    //create user
    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody User employeeCreatedUser) {
        boolean isExistByPhoneNumberAndRole = userRepository.existsByPhoneNumberAndRole(employeeCreatedUser.getPhoneNumber(), employeeCreatedUser.getRole());
        if (isExistByPhoneNumberAndRole == true) {
            return new ResponseEntity<>(new CreatedSuccessApi(true, "Số điện thoại đã được đăng kí.", false), HttpStatus.OK);
        }
        if (employeeCreatedUser.getRole().equals("ADMIN")) {
            boolean isCoordinator = userRepository.existsByPhoneNumberAndRole(employeeCreatedUser.getPhoneNumber(), "COORDINATOR");
            if (isCoordinator == true) {
                return new ResponseEntity<>(new CreatedSuccessApi(true, "Số điện thoại không được đăng ký với vị trí này.", false), HttpStatus.OK);
            }
        }
        if (employeeCreatedUser.getRole().equals("COORDINATOR")) {
            boolean isCoordinator = userRepository.existsByPhoneNumberAndRole(employeeCreatedUser.getPhoneNumber(), "ADMIN");
            if (isCoordinator == true) {
                return new ResponseEntity<>(new CreatedSuccessApi(true, "Số điện thoại này không được thực hiện đăng kí.", false), HttpStatus.OK);
            }
        }
        String enCryptPassword = bCryptPasswordEncoder.encode(employeeCreatedUser.getPassword());
        employeeCreatedUser.setActive(1);
        employeeCreatedUser.setImage(employeeCreatedUser.getImage());
        employeeCreatedUser.setPassword(enCryptPassword);
        userService.saveUser(employeeCreatedUser);
        SmsRequest smsRequest = new SmsRequest(employeeCreatedUser.getPhoneNumber(), employeeCreatedUser.getRole());
        smsService.verifySms(smsRequest);
        return new ResponseEntity<>(employeeCreatedUser, HttpStatus.OK);
    }

    //login in web (for coordinator/admin)
    @PostMapping("/login")
    public ResponseEntity<?> webLogin(@RequestBody WebUserLoginModel webUserLoginModel) {
        boolean isCoordinator = userRepository.existsByPhoneNumberAndRole(webUserLoginModel.getPhoneNumber(), "COORDINATOR");
        boolean isAdmin = userRepository.existsByPhoneNumberAndRole(webUserLoginModel.getPhoneNumber(), "ADMIN");
        if (isCoordinator == false && isAdmin == false) {
            return new ResponseEntity<>(new LoginResponse(true, "Số điện thoại không tồn tại.", false), HttpStatus.OK);
        }
        User userByPhone;
        if (isAdmin == true) {
            userByPhone = userRepository.getUserByPhoneNumberAndRole(webUserLoginModel.getPhoneNumber(), "ADMIN");
        } else {
            userByPhone = userRepository.getUserByPhoneNumberAndRole(webUserLoginModel.getPhoneNumber(), "COORDINATOR");
        }
        if (!BCrypt.checkpw(webUserLoginModel.getPassword(), userByPhone.getPassword())) {
            return new ResponseEntity<>(new LoginResponse(true, "Sai mật khẩu", false), HttpStatus.OK);
        }
        //create BEARER token
        String token = Jwts.builder()
                .setSubject(webUserLoginModel.getPhoneNumber())
                .setExpiration(new Date(System.currentTimeMillis() + SecurityUtils.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityUtils.SECRET.getBytes())
                .compact();

        User successfulUser = (userRepository.getUserByPhoneNumberAndRole(userByPhone.getPhoneNumber(), userByPhone.getRole()));
        LoginAccountModel loginAccountModel = new LoginAccountModel();
        loginAccountModel.setUserInfo(successfulUser);
        loginAccountModel.setToken(token);
        return new ResponseEntity<>(loginAccountModel, HttpStatus.OK);
    }

    //get 1 user
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> userInfo(@PathVariable("id") int userID) {
        Optional<User> getUserByID = userService.getUserByID(userID);
        if (!getUserByID.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng"), HttpStatus.OK);
        }
        return new ResponseEntity<>(getUserByID, HttpStatus.OK);
    }

    //update 1 user
    @PostMapping("/update-user/{id}")
    public ResponseEntity<?> updateUserInfo(@RequestBody User user, @PathVariable("id") int userID) {
        Optional<User> getUserByID = userService.getUserByID(userID);
        if (!getUserByID.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng"), HttpStatus.OK);
        }
        user.setId(userID);
        userService.updateContainStatus(user);
        return new ResponseEntity<>(getUserByID, HttpStatus.OK);
    }


}


