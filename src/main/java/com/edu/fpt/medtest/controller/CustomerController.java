package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class CustomerController {

    @Autowired
    private UserService userService;

    //customer register
    @PostMapping("/customers/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        List<User> users = userService.getListUser();
        for (User userTrack : users) {
            if (userTrack.getPhoneNumber().equals(user.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
            } else {
                user.setActive(1);
                user.setAddress(null);
                user.setRole("Customer");
                user.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
                user.setTownCode(null);
                user.setDistrictCode(null);
            }
        }
        userService.saveUser(user);
        return new ResponseEntity<>(new ApiResponse(false, "Successfully registered"), HttpStatus.OK);
    }

    //list all customer
    @GetMapping("/customers/list")
    public ResponseEntity<?> list() {
        List<User> users = userService.getListUser();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get customer - view detail
    @GetMapping(value = "/customers/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update customer
//    @PatchMapping(value = "/customers/detail/update/{id}")
////    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") int id) {
////        Optional<User> getUser = userService.findUserByID(id);
////        if (!getUser.isPresent()) {
////            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
////        }
////            User existingUser = getUser.get();
////        System.out.println(user);
////        user.setId(id);
////        userService.saveUser(user);
////        return new ResponseEntity<>(new ApiResponse(true, "Update user successfully"), HttpStatus.OK);
////    }

}
