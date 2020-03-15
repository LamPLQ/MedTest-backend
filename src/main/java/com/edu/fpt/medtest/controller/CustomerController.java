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
    public ResponseEntity<?> register(@RequestBody User user){
        List<User> users = userService.getListUser();
        for(User userTrack: users){
            if(userTrack.getPhoneNumber().equals(user.getPhoneNumber())){
                return new ResponseEntity<>(new ApiResponse(false,"Phone number is already taken"), HttpStatus.BAD_REQUEST);
            }else{
                user.setActive(1);
                user.setAddress(null);
                user.setRole("Customer");
                user.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
                user.setTownCode(null);
                user.setDistrictCode(null);
            }
        }
        userService.saveUser(user);
        return new ResponseEntity<>(new ApiResponse(true,"Successfully registered"), HttpStatus.OK);
    }

    //list all customer
    @GetMapping("/customers/list")
    public ResponseEntity<?> list(){
        List<User> users = userService.getListUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //forgot password
//    @PutMapping("/customers/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody User user){
//        List<User> users = userService.getListUser();
//        for(User userTracking: users){
//            if(!userTracking.getPhoneNumber().equals(user.getPhoneNumber())&&userTracking.getEmail().equals(user.getEmail())){
//                System.out.println(user);
//                System.out.println(userTracking);
//                return new ResponseEntity<>(new ApiResponse(false,"Error phone number or email"), HttpStatus.BAD_REQUEST);
//            }else{
//                user.setID(userTracking.getID());
//                user.setPassword("medtest123");
//            }
//        }
//        userService.saveUser(user);
//        return new ResponseEntity<>(new ApiResponse(true,"Password set to default: medtest123 "), HttpStatus.OK);
//    }

    //get customer - view detail
    @GetMapping(value = "/customers/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        return new ResponseEntity<>(getUser,HttpStatus.OK);
    }

}
