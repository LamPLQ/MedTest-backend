package com.edu.fpt.medtest.controller.Users;


import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.ChangePasswordModel;
import com.edu.fpt.medtest.model.LoginAccountModel;
import com.edu.fpt.medtest.model.LoginModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.security.SecurityUtils;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.ComfirmResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/coordinators")
public class CoordinatorController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginModel loginUser) {
        try {
            boolean existByPhoneNumberAndRole = userRepository.existsByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
            if (!existByPhoneNumberAndRole == true) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            User userLogin = userRepository.getUserByPhoneNumberAndRole(loginUser.getPhoneNumber(), loginUser.getRole());
            //check password
            if (!BCrypt.checkpw(loginUser.getPassword(), userLogin.getPassword())) {
                return new ResponseEntity<>(new ApiResponse(true, "Sai mật khẩu!"), HttpStatus.OK);
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
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //coordinator register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User coordinator) {
        try {
            boolean existByPhoneAndRole = userRepository.existsByPhoneNumberAndRole(coordinator.getPhoneNumber(), "COORDINATOR");
            if (existByPhoneAndRole == true) {
                return new ResponseEntity<>(new ApiResponse(true, "Số điện thoại đã tồn tại"), HttpStatus.OK);
            }
            coordinator.setActive(1);
            coordinator.setAddress(null);
            coordinator.setRole("COORDINATOR");
            coordinator.setImage(coordinator.getImage());
            coordinator.setTownCode(null);
            coordinator.setDistrictCode(null);
            coordinator.setPassword(bCryptPasswordEncoder.encode(coordinator.getPassword()));
            userService.saveUser(coordinator);
            return new ResponseEntity<>(new ApiResponse(true, "Đã đăng kí thành công"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //list all coordinators
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        try {
            List<User> users = userRepository.findAllByRole("COORDINATOR");
            if (users.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy bất kì điều phối viên nào!"), HttpStatus.OK);
            }
            List<User> returnCoordinator = new ArrayList<>();
            for (User user : users.subList(1, users.size())) {
                returnCoordinator.add(user);
            }
            return new ResponseEntity<>(returnCoordinator, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //get coordinators - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        try {
            Optional<User> getCoordinator = userRepository.getUserByIdAndRole(id, "COORDINATOR");
            if (!getCoordinator.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            if(getCoordinator.get().getActive() == 0){
                return new ResponseEntity<>(new ApiResponse(false,"Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(getCoordinator, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //update coordinator info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User coordinator, @PathVariable("id") int id) {
        try {
            Optional<User> getCoordinator = userRepository.getUserByIdAndRole(id, "COORDINATOR");
            if (!getCoordinator.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            if(getCoordinator.get().getActive() == 0){
                return new ResponseEntity<>(new ApiResponse(false,"Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            coordinator.setId(id);
            userService.update(coordinator);
            return new ResponseEntity<>(getCoordinator, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) {
        try {
            Optional<User> getCustomer = userRepository.getUserByIdAndRole(id, "COORDINATOR");
            if (!getCustomer.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            if(getCustomer.get().getActive() == 0){
                return new ResponseEntity<>(new ApiResponse(false,"Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getCustomer.get().getPassword())) {
                return new ResponseEntity<>(new ApiResponse(true, "Mật khẩu hiện tại không đúng!"), HttpStatus.OK);
            }
            if(changePasswordModel.getOldPassword().equals(changePasswordModel.getNewPassword())){
                return new ResponseEntity<>(new ComfirmResponse(true,"Mật khẩu mới phải khác mật khẩu cũ", false),HttpStatus.OK);
            }
            changePasswordModel.setID(id);
            getCustomer.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
            userService.saveUser(getCustomer.get());
            return new ResponseEntity<>(new ApiResponse(true, "Thay đổi mật khẩu thành công!"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}