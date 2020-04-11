package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Notification;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.ForgotPasswordModel;
import com.edu.fpt.medtest.model.SentMailModel;
import com.edu.fpt.medtest.repository.DistrictRepository;
import com.edu.fpt.medtest.repository.TownRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.MailService;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private SentMailModel sentMailModel;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // List user with state ACTIVE
    @GetMapping("/list/active")
    public ResponseEntity<?> listActive() {
        List<User> lsUsersActive = userService.lsUserActive();
        if (lsUsersActive.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no user active"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsUsersActive, HttpStatus.OK);
    }

    // List user with state DE-ACTIVE
    @GetMapping("/list/deactive")
    public ResponseEntity<?> listDeactive() {
        List<User> lsUsersDeactive = userService.lsUserNotActive();
        if (lsUsersDeactive.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "There is no user deactive"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsUsersDeactive, HttpStatus.OK);
    }

    //List user in 1 district
    @GetMapping("/list/district/{code}")
    public ResponseEntity<?> listDistrict(@PathVariable("code") String code) {
        List<User> lsByDistrict = userService.lsUserByDistrict(code);
        Optional<District> district = districtRepository.findById(code);
        if (lsByDistrict.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "There is no user in district " + district.get().getDistrictName()), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lsByDistrict, HttpStatus.OK);
    }

    //List user in 1 town
    @GetMapping("/list/town/{code}")
    public ResponseEntity<?> listTown(@PathVariable("code") String code) {
        List<User> lsByTown = userService.lsUserByTown(code);
        Optional<Town> town = townRepository.findById(code);
        if (lsByTown.isEmpty())
            return new ResponseEntity<>(new ApiResponse(true, "There is no user in district " + town.get().getTownName()), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(lsByTown, HttpStatus.OK);
    }

    //Reset password for user
    @PostMapping("/reset-password/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable("id") int id) {
        Optional<User> userByID = userService.getUserByID(id);
        if(!userByID.isPresent()){
            return new ResponseEntity<>(new ApiResponse(true,"Không tồn tại người dùng này"), HttpStatus.OK);
        }
        if(userByID.get().getRole().equals("CUSTOMER")){
            return new ResponseEntity<>(new ApiResponse(true,"Người dùng hiện tại không thực hiện được chức năng này"), HttpStatus.OK);
        }
        sentMailModel.setEmail(userByID.get().getEmail());
        sentMailModel.setPhoneNumber(userByID.get().getPhoneNumber());
        sentMailModel.setRole(userByID.get().getRole());
        try{
            mailService.sendEmail(sentMailModel);
        }catch (MailException mailException){
            System.out.println(mailException);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu mới đã được gửi đến email " + userByID.get().getEmail() + "của userID = " + userByID.get().getEmail()), HttpStatus.OK);
    }

    //forgotPassword
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel) {
        boolean existPhoneNumber = userRepository.existsByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
        if (!existPhoneNumber == true) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy số điện thoại đã nhập!"), HttpStatus.OK);
        }
        User forgotPasswordUser = userService.getUserByPhoneNumberAndRole(forgotPasswordModel.getPhoneNumber(), "CUSTOMER");
        sentMailModel.setEmail(forgotPasswordUser.getEmail());
        sentMailModel.setPhoneNumber(forgotPasswordUser.getPhoneNumber());
        sentMailModel.setRole(forgotPasswordUser.getRole());
        try {
            mailService.sendEmail(sentMailModel);
        } catch (MailException mailException) {
            System.out.println(mailException);
        }
        return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu mới đã được gửi đến email bạn đã đăng kí!"), HttpStatus.OK);
    }

    //list notification for user
    @GetMapping("{id}/notifications/list")
    public ResponseEntity<?> getLsNotification(@PathVariable("id") int userID) {
        List<Notification> lsNoti = notificationService.lsNotification(userID);
        if (lsNoti.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Do not have any notification yet"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(lsNoti, HttpStatus.OK);
    }

    //create a new notification
    @PostMapping("/notifications/create")
    public ResponseEntity<?> createNoti(@RequestBody Notification notification) {
        notificationService.saveNoti(notification);
        return new ResponseEntity<>(new ApiResponse(true, "Created Notification successfully"), HttpStatus.OK);
    }

    //list all user
    @GetMapping("/list-all-user")
    public ResponseEntity listAllUser(){
        List<User> lsAllUser = userService.getListUser();
        if (lsAllUser.isEmpty()){
            return new ResponseEntity(new ApiResponse(true,"Không có người dùng trong hệ thống!"), HttpStatus.OK);
        }
        List<User> returnList = new ArrayList<>();
        for (User user:lsAllUser.subList(1,lsAllUser.size())){
            returnList.add(user);
        }
        if (returnList.isEmpty()){
            return new ResponseEntity(new ApiResponse(true,"Không có người dùng!"), HttpStatus.OK);
        }
        return new ResponseEntity(returnList,HttpStatus.OK) ;
    }
}


