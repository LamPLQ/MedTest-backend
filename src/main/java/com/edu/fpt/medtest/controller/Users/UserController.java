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

   /* @Autowired
    private CheckEmailExist checkEmailExist;*/

    // List user with state ACTIVE
    @GetMapping("/list/active")
    public ResponseEntity<?> listActive() {
        try {
            List<User> lsUsersActive = userService.lsUserActive();
            if (lsUsersActive.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở trạng thái đang hoạt động!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsUsersActive, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Lỗi");
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    // List user with state DE-ACTIVE
    @GetMapping("/list/deactive")
    public ResponseEntity<?> listDeactive() {
        try {
            List<User> lsUsersDeactive = userService.lsUserNotActive();
            if (lsUsersDeactive.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở trạng thái khoá!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsUsersDeactive, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //List user in 1 district
    @GetMapping("/list/district/{code}")
    public ResponseEntity<?> listDistrict(@PathVariable("code") String code) {
        try {
            List<User> lsByDistrict = userService.lsUserByDistrict(code);
            Optional<District> district = districtRepository.findById(code);
            if (lsByDistrict.isEmpty())
                return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng tại quận/huyện " + district.get().getDistrictName()), HttpStatus.OK);
            return new ResponseEntity<>(lsByDistrict, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //List user in 1 town
    @GetMapping("/list/town/{code}")
    public ResponseEntity<?> listTown(@PathVariable("code") String code) {
        try {
            List<User> lsByTown = userService.lsUserByTown(code);
            Optional<Town> town = townRepository.findById(code);
            if (lsByTown.isEmpty())
                return new ResponseEntity<>(new ApiResponse(true, "Không có người dùng nào ở phường/huyện " + town.get().getTownName()), HttpStatus.OK);
            return new ResponseEntity<>(lsByTown, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //Reset password for user
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@RequestBody UserProcessingModel userProcessingModel, @PathVariable("id") int id) {
        try {
            try {
                if (userProcessingModel.getUserProcessingID() == 0) {
                    return new ResponseEntity<>(new ComfirmResponse(true, "Không xác định được tài khoản đang thao tác!", false), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Không xác định được tài khoản đang thao tác!", false), HttpStatus.OK);
            }
            Optional<User> processingUser = userService.getUserByID(userProcessingModel.getUserProcessingID());
            if (!processingUser.isPresent()) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Không tồn tại tài khoản!", false), HttpStatus.OK);
            }
            if (!processingUser.get().getRole().equals("ADMIN")) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Người dùng hiện tại không được thực hiện được chức năng này!", false), HttpStatus.OK);
            }
            Optional<User> userByID = userService.getUserByID(id);
            if (!userByID.isPresent()) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Không tồn tại người dùng cần thay đổi mật khẩu!", false), HttpStatus.OK);
            }
            String oldPassword = userByID.get().getPassword();
            //System.out.println(oldPassword + " old");
            SmsRequest smsRequest = new SmsRequest(userByID.get().getPhoneNumber(), userByID.get().getRole());
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
            smsService.resetPassword(smsRequest);
            Optional<User> userUpdated = Optional.ofNullable(userRepository.getUserByPhoneNumberAndRole(userByID.get().getPhoneNumber(), userByID.get().getRole()));
            String newPassword = userUpdated.get().getPassword();
            //System.out.println(newPassword);
            if (oldPassword.equals(newPassword)) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Số điện thoại không đúng! ", false), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ComfirmResponse(true, "Mật khẩu mới đã được gửi đến số điện thoại " + userByID.get().getPhoneNumber(), true), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //forgotPassword for customer
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel) {
        try {
            try {
                if (forgotPasswordModel.getPhoneNumber().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Không được để trống số điện thoại!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Không được để trống số điện thoại!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(forgotPasswordModel.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
            }
            boolean existPhoneNumber = userRepository.existsByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
            if (!existPhoneNumber == true) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Không tìm thấy số điện thoại đã nhập!", false), HttpStatus.OK);
            }
            User forgotPasswordUser = userService.getUserByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
            if (forgotPasswordUser.getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            String oldPassword = forgotPasswordUser.getPassword();
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
            Optional<User> afterResetPassword = Optional.ofNullable(userRepository.getUserByPhoneNumberAndRole(forgotPasswordUser.getPhoneNumber(), forgotPasswordUser.getRole()));
            String newPassword = afterResetPassword.get().getPassword();
            if (oldPassword.equals(newPassword)) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Số điện thoại không đúng! ", false), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ComfirmResponse(true, "Mật khẩu mới đã được gửi đến số điện thoại " + forgotPasswordUser.getPhoneNumber(), true), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list notification for user
    @GetMapping("{id}/notifications/list")
    public ResponseEntity<?> getLsNotification(@PathVariable("id") int userID) {
        try {
            Optional<User> createUser = userRepository.findById(userID);
            if (!createUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            if (createUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            List<Notification> lsNoti = notificationService.lsNotification(userID);
            if (lsNoti.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Hiện tại không có thông báo nào!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(lsNoti, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex);
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list all user
    @GetMapping("/list-all-user")
    public ResponseEntity listAllUser() {
        try {
            List<User> lsAllUser = userService.getListUser();
            if (lsAllUser.isEmpty()) {
                return new ResponseEntity(new ApiResponse(true, "Không có người dùng trong hệ thống!"), HttpStatus.OK);
            }
        /*List<User> returnList = new ArrayList<>();
        for (User user : lsAllUser.subList(1, lsAllUser.size())) {
            returnList.add(user);
        }
        if (returnList.isEmpty()) {
            return new ResponseEntity(new ApiResponse(true, "Không có người dùng!"), HttpStatus.OK);
        }*/
            return new ResponseEntity(lsAllUser, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //send OTP to phone by messsing
    //      to verify phone is true
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendSmS(@Valid @RequestBody SmsRequest smsRequest) {
        try {
            try {
                if (smsRequest.getPhoneNumber().isEmpty() || smsRequest.getRole().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Phải điền đủ thông tin để đăng kí!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Phải điền đủ thông tin để đăng kí!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(smsRequest.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng"), HttpStatus.OK);
            }
            if (!smsRequest.getRole().equals("CUSTOMER")) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được người dùng!"), HttpStatus.OK);
            }
            boolean existByPhoneAndRole = userRepository.existsByPhoneNumberAndRole(smsRequest.getPhoneNumber(), smsRequest.getRole());
            if (existByPhoneAndRole == true) {
                return new ResponseEntity<>(new SendMessageResponse(true, "Số điện thoại đã tồn tại!", false), HttpStatus.OK);
            }
            List<ValidPhoneToken> lsAllTokenOfPhoneNumber = tokenRepository.getAllByPhoneNumber(smsRequest.getPhoneNumber());
            for (ValidPhoneToken validPhoneToken : lsAllTokenOfPhoneNumber) {
                tokenRepository.delete(validPhoneToken);
            }
            smsService.sendSms(smsRequest);
            List<ValidPhoneToken> lsValidPhoneToken = tokenRepository.getAllByPhoneNumber(smsRequest.getPhoneNumber());
            if (lsValidPhoneToken.isEmpty() || lsValidPhoneToken == null) {
                return new ResponseEntity<>(new SendMessageResponse(true, "Số điện thoại không đúng. Vui lòng nhập số điện thoại hợp lệ hoặc gọi đến phòng khám để xử lý!", false), HttpStatus.OK);
            }
            return new ResponseEntity<>(new SendMessageResponse(true, "Gửi thành công tin nhắn đến số điện thoại.", true), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //check OPT is valid
    //              valid=true -> insert 1 object user -> message insert successful
    //              valid=false -> return message
    @PostMapping("/valid-phone-otp")
    public ResponseEntity<?> isValidPhoneNumberOTP(@RequestBody CheckOTPModel checkOTPModel) {
        try {
            try {
                if (checkOTPModel.getOtp().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Cần nhập mã OTP"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Cần nhập mã OTP"), HttpStatus.OK);
            }
            if (!Validate.isValidNumber(checkOTPModel.getOtp())) {
                return new ResponseEntity<>(new ApiResponse(false, "Mã OTP phải là số!"), HttpStatus.OK);
            }
            if (!(checkOTPModel.getOtp().length() == 6)) {
                return new ResponseEntity<>(new ApiResponse(false, "Mã OTP phải có 6 kí tự!"), HttpStatus.OK);
            }
            try {
                if (checkOTPModel.getPhoneNumber().isEmpty() ||
                        checkOTPModel.getName().isEmpty() ||
                        checkOTPModel.getDob().toString().isEmpty() ||
                        checkOTPModel.getPassword().isEmpty() ||
                        checkOTPModel.getEmail().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Cần nhập đủ thông tin người dùng!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Cần nhập đủ thông tin người dùng!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(checkOTPModel.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
            }
            if (!Validate.isValidUserName(checkOTPModel.getName())) {
                return new ResponseEntity<>(new ApiResponse(false, "Tên người dùng chỉ bao gồm kí tự chữ và ít hơn 50 kí tự!"), HttpStatus.OK);
            }
            if(checkOTPModel.getName().length()>50){
                return new ResponseEntity<>(new ApiResponse(false, "Tên hiển thị quá dài! Hãy điền tên ngắn hơn!"), HttpStatus.OK);
            }
            if (!Validate.isValidEmail(checkOTPModel.getEmail())) {
                return new ResponseEntity<>(new ApiResponse(false, "Email không đúng định dạng!"), HttpStatus.OK);
            }
            if (!(checkOTPModel.getGender() == 0 || checkOTPModel.getGender() == 1)) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được giới tính người dùng!"), HttpStatus.OK);
            }
            if (checkOTPModel.getPassword().length() < 6) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải lớn hơn 6 kí tự"), HttpStatus.OK);
            }
            Optional<ValidPhoneToken> checkValidPhoneToken = tokenRepository.getByPhoneNumberAndToken(checkOTPModel.getPhoneNumber(), checkOTPModel.getOtp());
            if (!checkValidPhoneToken.isPresent()) {
                return new ResponseEntity<>(new CheckOTPResponse(true, "Mã OTP không hợp lệ!", false, false), HttpStatus.OK);
            }
            if (checkValidPhoneToken.get().getExpiredTime() <= System.currentTimeMillis()) {
                //tokenRepository.delete(tokenRepository.getOne(checkValidPhoneToken.get().getSessionID()));
                return new ResponseEntity<>(new CheckOTPResponse(true, "Mã OTP hết hạn!", false, false), HttpStatus.OK);
            }

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
            registeredUser.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
            registeredUser.setTownCode(null);
            registeredUser.setDistrictCode(null);
            registeredUser.setPassword(enCryptPassword);
            userService.saveUser(registeredUser);
            tokenRepository.delete(tokenRepository.getOne(checkValidPhoneToken.get().getSessionID()));
            return new ResponseEntity<>(new CheckOTPResponse(true, "Nhập mã OTP thành công. Người dùng đăng kí thành công!", true, true), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //resendOTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody SmsRequest smsRequest) {
        try {
            try {
                if (smsRequest.getPhoneNumber().isEmpty() || smsRequest.getRole().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Phải điền đủ thông tin để lấy lại mã!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(new ApiResponse(false, "Phải điền đủ thông tin để lấy lại mã!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(smsRequest.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng"), HttpStatus.OK);
            }
            if (!smsRequest.getRole().equals("CUSTOMER")) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được người dùng!"), HttpStatus.OK);
            }
            Optional<ValidPhoneToken> validPhoneToken = tokenRepository.getByPhoneNumber(smsRequest.getPhoneNumber());
            if (!validPhoneToken.isPresent()) {
                return new ResponseEntity<>(new SendMessageResponse(true, "Bạn chưa đăng kí MedTest với số điện thoại này!", false), HttpStatus.OK);
            }
            tokenRepository.delete(validPhoneToken.get());
            smsService.sendSms(smsRequest);
            return new ResponseEntity<>(new SendMessageResponse(true, "Đã gửi lại mã OTP tới đến số điện thoại.", true), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create user
    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody User employeeCreatedUser) {
        try {
            try {
                try {
                    if (employeeCreatedUser.getName().isEmpty() ||
                            employeeCreatedUser.getPhoneNumber().isEmpty() ||
                            employeeCreatedUser.getEmail().isEmpty() ||
                            employeeCreatedUser.getDob().toString().isEmpty() ||
                            employeeCreatedUser.getPassword().isEmpty() ||
                            employeeCreatedUser.getRole().isEmpty() ||
                            employeeCreatedUser.getDistrictCode().isEmpty() ||
                            employeeCreatedUser.getTownCode().isEmpty() ||
                            employeeCreatedUser.getAddress().isEmpty()) {
                        return new ResponseEntity<>(new ApiResponse(false, "Không thể tạo người dùng mới vì thiếu thông tin"), HttpStatus.OK);
                    }
                } catch (Exception ex) {
                    return new ResponseEntity<>(new ApiResponse(false, "Không thể tạo người dùng mới vì thiếu thông tin"), HttpStatus.OK);
                }
                if (!Validate.isPhoneNumber(employeeCreatedUser.getPhoneNumber())) {
                    return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
                }
                if (!Validate.isValidUserName(employeeCreatedUser.getName())) {
                    return new ResponseEntity<>(new ApiResponse(false, "Tên người dùng chỉ được chứa kí tự chữ và ít hơn 50 kí tự!"), HttpStatus.OK);
                }
                if(employeeCreatedUser.getName().length()>50){
                    return new ResponseEntity<>(new ApiResponse(false, "Tên hiển thị quá dài! Hãy điền tên ngắn hơn!"), HttpStatus.OK);
                }
                if (!Validate.isValidEmail(employeeCreatedUser.getEmail())) {
                    return new ResponseEntity<>(new ApiResponse(false, "Email không đúng định dạng!"), HttpStatus.OK);
                }
                if (employeeCreatedUser.getPassword().length() < 6) {
                    return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải nhiều hơn 6 kí tự!"), HttpStatus.OK);
                }
                if (!(employeeCreatedUser.getRole().equals("NURSE") ||
                        employeeCreatedUser.getRole().equals("COORDINATOR") ||
                        employeeCreatedUser.getRole().equals("ADMIN"))) {
                    return new ResponseEntity<>(new ApiResponse(false, "Không xác định được người dùng!"), HttpStatus.OK);
                }
                if (districtRepository.existsByDistrictCode(employeeCreatedUser.getDistrictCode()) == false) {
                    return new ResponseEntity<>(new ApiResponse(false, "Mã quận/huyện không tồn tại!"), HttpStatus.OK);
                }
                if (townRepository.existsByTownCodeAndDistrictCode(employeeCreatedUser.getTownCode(), employeeCreatedUser.getDistrictCode()) == false) {
                    return new ResponseEntity<>(new ApiResponse(false, "Mã phường/xã không đúng!"), HttpStatus.OK);
                }
                if(employeeCreatedUser.getAddress().length()>90){
                    return new ResponseEntity<>(new ApiResponse(false, "Địa chỉ quá dài! Hãy điền tên ngắn hơn!"), HttpStatus.OK);
                }
                boolean isExistByPhoneNumberAndRole = userRepository.existsByPhoneNumberAndRole(employeeCreatedUser.getPhoneNumber(), employeeCreatedUser.getRole());
                if (isExistByPhoneNumberAndRole) {
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
                if (employeeCreatedUser.getImage().isEmpty() || employeeCreatedUser.getImage() == null) {
                    employeeCreatedUser.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
                }
                employeeCreatedUser.setPassword(enCryptPassword);
                SmsRequest smsRequest = new SmsRequest(employeeCreatedUser.getPhoneNumber(), employeeCreatedUser.getRole());
                smsService.verifySms(smsRequest);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return new ResponseEntity<>(new CreatedSuccessApi(false, "Số điện thoại không tồn tại!", false), HttpStatus.OK);
            }
            try {
                userService.saveUser(employeeCreatedUser);
            } catch (Exception ex) {
                ex.getMessage();
                return new ResponseEntity<>(new CreatedSuccessApi(true, "Số điện thoại đã được đăng kí.", false), HttpStatus.OK);
            }
            return new ResponseEntity<>(employeeCreatedUser, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //login in web (for coordinator/admin)
    @PostMapping("/login")
    public ResponseEntity<?> webLogin(@RequestBody WebUserLoginModel webUserLoginModel) {
        try {
            try {
                if (webUserLoginModel.getPhoneNumber().isEmpty() || webUserLoginModel.getPassword().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Không được để trống các trường đăng nhập!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Không được để trống các trường đăng nhập!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(webUserLoginModel.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
            }
            if (webUserLoginModel.getPassword().length() < 6) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải có ít nhất 6 kí tự!"), HttpStatus.OK);
            }
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
            if (userByPhone.getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
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
            //LoginAccountModel loginAccountModel = new LoginAccountModel();
            //loginAccountModel.setUserInfo(successfulUser);
            //loginAccountModel.setToken(token);
            //return new ResponseEntity<>(loginAccountModel, HttpStatus.OK);
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
            return new ResponseEntity<>(loginAccountModel,HttpStatus.OK);
            //
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get 1 user
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> userInfo(@PathVariable("id") int userID) {
        try {
            Optional<User> getUserByID = userService.getUserByID(userID);
            if (!getUserByID.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getUserByID, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update 1 user
    @PostMapping("/update-user/{id}")
    public ResponseEntity<?> updateUserInfo(@RequestBody User user, @PathVariable("id") int userID) {
        try {
            try {
                if (user.getName().isEmpty() ||
                        user.getDob().toString().isEmpty() ||
                        user.getAddress().isEmpty() ||
                        user.getEmail().isEmpty() ||
                        user.getTownCode().isEmpty() ||
                        user.getDistrictCode().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Phải nhập đủ dữ liệu để cập nhật người dùng!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Phải nhập đủ dữ liệu để cập nhật người dùng!"), HttpStatus.OK);
            }
            if (!Validate.isValidUserName(user.getName())) {
                return new ResponseEntity<>(new ApiResponse(false, "Tên người dùng chỉ bao gồm kí tự chữ và ít hơn 50 kí tự!"), HttpStatus.OK);
            }
            if(user.getName().length()>50){
                return new ResponseEntity<>(new ApiResponse(false, "Tên hiển thị quá dài! Hãy điền tên ngắn hơn!"), HttpStatus.OK);
            }
            if (!Validate.isValidEmail(user.getEmail())) {
                return new ResponseEntity<>(new ApiResponse(false, "Email không đúng định dạng!"), HttpStatus.OK);
            }
            if (districtRepository.existsByDistrictCode(user.getDistrictCode()) == false) {
                return new ResponseEntity<>(new ApiResponse(false, "Mã quận/huyện không tồn tại!"), HttpStatus.OK);
            }
            if (townRepository.existsByTownCodeAndDistrictCode(user.getTownCode(), user.getDistrictCode()) == false) {
                return new ResponseEntity<>(new ApiResponse(false, "Mã phường/xã không đúng!"), HttpStatus.OK);
            }
            if(user.getAddress().length()>90){
                return new ResponseEntity<>(new ApiResponse(false, "Địa chỉ quá dài! Hãy điền tên ngắn hơn!"), HttpStatus.OK);
            }
            if (!(user.getActive() == 1 || user.getActive() == 0)) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được trạng thái người dùng!"), HttpStatus.OK);
            }
            if (!(user.getGender() == 1 || user.getGender() == 0)) {
                return new ResponseEntity<>(new ApiResponse(false, "Không xác định được giới tính người dùng!"), HttpStatus.OK);
            }
            Optional<User> getUserByID = userService.getUserByID(userID);
            if (!getUserByID.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy người dùng"), HttpStatus.OK);
            }
            try {
                if (user.getImage().isEmpty()) {
                    user.setId(userID);
                    userService.updateContainStatus(user);
                } else {
                    user.setId(userID);
                    userService.updateContainImage(user);
                }
            } catch (NullPointerException ex) {
                user.setId(userID);
                userService.updateContainStatus(user);
            }
            return new ResponseEntity<>(getUserByID, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Oops! Lỗi hệ thống! Xin vui lòng thử lại!"), HttpStatus.OK);
        }
    }

    @PostMapping("admin/change-password/{id}")
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
            Optional<User> getAdmin = userRepository.getUserByIdAndRole(id, "ADMIN");
            if (!getAdmin.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            if (getAdmin.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getAdmin.get().getPassword())) {
                return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu hiện tại không đúng!"), HttpStatus.OK);
            }
            if (changePasswordModel.getOldPassword().equals(changePasswordModel.getNewPassword())) {
                return new ResponseEntity<>(new ComfirmResponse(true, "Mật khẩu mới phải khác mật khẩu cũ", false), HttpStatus.OK);
            }
            changePasswordModel.setID(id);
            getAdmin.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
            userService.saveUser(getAdmin.get());
            return new ResponseEntity<>(new ApiResponse(true, "Thay đổi mật khẩu thành công!"), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    @PostMapping("/test-hash")
    public ResponseEntity<?> testHash(@RequestBody WebUserLoginModel webUserLoginModel) {
        try {
            try {
                if (webUserLoginModel.getPhoneNumber().isEmpty() || webUserLoginModel.getPassword().isEmpty()) {
                    return new ResponseEntity<>(new ApiResponse(false, "Không được để trống các trường đăng nhập!"), HttpStatus.OK);
                }
            } catch (Exception ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Không được để trống các trường đăng nhập!"), HttpStatus.OK);
            }
            if (!Validate.isPhoneNumber(webUserLoginModel.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Số điện thoại không đúng định dạng!"), HttpStatus.OK);
            }
            if (webUserLoginModel.getPassword().length() < 6) {
                return new ResponseEntity<>(new ApiResponse(false, "Mật khẩu phải có ít nhất 6 kí tự!"), HttpStatus.OK);
            }
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
            if (userByPhone.getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
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
            //LoginAccountModel loginAccountModel = new LoginAccountModel();
            //loginAccountModel.setUserInfo(successfulUser);
            //loginAccountModel.setToken(token);
            //return new ResponseEntity<>(loginAccountModel, HttpStatus.OK);
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
            return new ResponseEntity<>(loginAccountModel,HttpStatus.OK);
            //
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}


