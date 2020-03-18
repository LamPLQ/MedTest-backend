package com.edu.fpt.medtest.controller.Users;


import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static com.edu.fpt.medtest.utils.EncodePassword.getSHA;
import static com.edu.fpt.medtest.utils.EncodePassword.toHexString;

@RestController
@RequestMapping("/users/nurses")
public class NurseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    //nurse register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User nurse) throws NoSuchAlgorithmException {
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
        nurse.setPassword(toHexString(getSHA(nurse.getPassword())));
        userService.saveUser(nurse);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
    }

    //list all nurse
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("NURSE");
        System.out.println(users);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get nurse - view detail info
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("NURSE")){
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update nurse info
    @PutMapping(value = "/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User nurse, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Nurse not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("NURSE")){
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        nurse.setId(id);
        userService.update(nurse);
        return new ResponseEntity<>(new ApiResponse(true, "Update nurse successfully"), HttpStatus.OK);
    }
}
