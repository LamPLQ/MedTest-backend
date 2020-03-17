package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.entity.Town;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.CustomerModel;
import com.edu.fpt.medtest.model.DistrictModel;
import com.edu.fpt.medtest.model.UserAppointment;
import com.edu.fpt.medtest.repository.AppointmentRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    //customer register
    @PostMapping("/customers/register")
    public ResponseEntity<?> register(@RequestBody User user) throws NoSuchAlgorithmException {
        List<User> users = userService.getListUser();
        for (User userTrack : users) {
            if (userTrack.getPhoneNumber().equals(user.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
            }
        }
        user.setActive(1);
        user.setAddress(null);
        user.setRole("Customer");
        user.setImage("https://www.kindpng.com/picc/m/10-104902_simple-user-icon-user-icon-white-png-transparent.png");
        user.setTownCode(null);
        user.setDistrictCode(null);
        user.setPassword(toHexString(getSHA(user.getPassword())));
        userService.saveUser(user);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
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
    @PutMapping(value = "/customers/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        user.setId(id);
        userService.update(user);
        return new ResponseEntity<>(new ApiResponse(true, "Update user successfully"), HttpStatus.OK);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws NoSuchAlgorithmException {
        boolean login = false;
        User userLogin = userRepository.getUserByPhoneNumber(user.getPhoneNumber());
        System.out.println(userLogin.getPassword());
        System.out.println(toHexString(getSHA(user.getPassword())));
        System.out.println(user.getPassword());
        if (userLogin.getPassword().equals(toHexString(getSHA(user.getPassword())))) {
            login = true;
        }
        return new ResponseEntity<>(login, HttpStatus.OK);
    }

    //view list appointment
    @GetMapping("/customers/{id}/appointments/list")
    public  ResponseEntity<?> getListAppointment(@RequestBody User user, @PathVariable("id") int id){
        List<Appointment> lsAppointmentCustomer = appointmentRepository.findAllByCustomerID(id);
        UserAppointment userAppointment = new UserAppointment();
        Optional<User> getUser =  userService.findUserByID(id);
        if (lsAppointmentCustomer.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        }
        //them phan chi tiet 1 appointment
        return new ResponseEntity<>(lsAppointmentCustomer,HttpStatus.OK);
    }




    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
