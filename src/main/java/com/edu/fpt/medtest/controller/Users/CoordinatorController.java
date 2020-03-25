package com.edu.fpt.medtest.controller.Users;


import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.ChangePasswordModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
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

    //coordinator register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User coordinator) throws NoSuchAlgorithmException {
        boolean existByPhoneNumber = userRepository.existsByPhoneNumber(coordinator.getPhoneNumber());
        if (existByPhoneNumber == true) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
        }
        coordinator.setActive(1);
        coordinator.setAddress(null);
        coordinator.setRole("COORDINATOR");
        coordinator.setImage(coordinator.getImage());
        coordinator.setTownCode(null);
        coordinator.setDistrictCode(null);
        coordinator.setPassword(bCryptPasswordEncoder.encode(coordinator.getPassword()));
        userService.saveUser(coordinator);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
    }

    //list all coordinators
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("COORDINATOR");
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get coordinators - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Coordinator not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("COORDINATOR")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update coordinator info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User coordinator, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Coordinator not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("COORDINATOR")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        coordinator.setId(id);
        userService.update(coordinator);
        return new ResponseEntity<>(new ApiResponse(true, "Update coordinator successfully"), HttpStatus.OK);
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) throws NoSuchAlgorithmException {
        Optional<User> getCoordinator = userService.findUserByID(id);
        if (!getCoordinator.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getCoordinator.get().getRole().equals("COORDINATOR")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        if (!BCrypt.checkpw(changePasswordModel.getOldPassword(), getCoordinator.get().getPassword())) {
            return new ResponseEntity<>(new ApiResponse(true, "Incorrect current password"), HttpStatus.BAD_REQUEST);
        }
        changePasswordModel.setID(id);
        getCoordinator.get().setPassword(bCryptPasswordEncoder.encode(changePasswordModel.getNewPassword()));
        userService.saveUser(getCoordinator.get());
        return new ResponseEntity<>(new ApiResponse(true, "Change password successfully!"), HttpStatus.OK);
    }
}
