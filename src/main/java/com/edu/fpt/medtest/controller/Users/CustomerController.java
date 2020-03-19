package com.edu.fpt.medtest.controller.Users;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.ChangePasswordModel;
import com.edu.fpt.medtest.model.UserAppointment;
import com.edu.fpt.medtest.repository.AppointmentRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.edu.fpt.medtest.utils.EncodePassword.getSHA;
import static com.edu.fpt.medtest.utils.EncodePassword.toHexString;

@RestController
@RequestMapping("/users/customers")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    //customer register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User customer) throws NoSuchAlgorithmException {
      /*List<User> users = userService.getListUser();
        for (User userTrack : users) {
            if (userTrack.getPhoneNumber().equals(customer.getPhoneNumber())) {
                return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
            }
        }*/
        boolean existByPhoneNumber = userRepository.existsByPhoneNumber(customer.getPhoneNumber());
        if (existByPhoneNumber == true) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number is already taken"), HttpStatus.NOT_FOUND);
        }
        customer.setActive(0);
        customer.setAddress(null);
        customer.setRole("CUSTOMER");
        customer.setImage(customer.getImage());
        customer.setTownCode(null);
        customer.setDistrictCode(null);
        customer.setPassword(toHexString(getSHA(customer.getPassword())));
        userService.saveUser(customer);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully registered"), HttpStatus.OK);
    }

    //list all customer
    @GetMapping("/list")
    public ResponseEntity<?> list() {
        List<User> users = userRepository.findAllByRole("CUSTOMER");
        System.out.println(users);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No user is found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //get customer - view detail info
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getUser, HttpStatus.OK);
    }

    //update customer info
    @PutMapping("/detail/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User customer, @PathVariable("id") int id) {
        Optional<User> getUser = userService.findUserByID(id);
        if (!getUser.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getUser.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        customer.setId(id);
        userService.update(customer);
        return new ResponseEntity<>(new ApiResponse(true, "Update user successfully"), HttpStatus.OK);
    }

    //view list appointment theo 1 customer
    @GetMapping("/{id}/appointments/list")
    public ResponseEntity<?> getListAppointment(@PathVariable("id") int id) {
        List<Appointment> lsAppointmentCustomer = appointmentRepository.findAllByCustomerID(id);
//        UserAppointment userAppointment = new UserAppointment();
        User userAppoint = userService.findUserByID(id).get();
        if (lsAppointmentCustomer.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        }
        //list detail of each appoinment which belong to user
        List<UserAppointment> listUserAppoinment = new ArrayList<>();
        for (Appointment appointments : lsAppointmentCustomer) {
            UserAppointment userAppointment = new UserAppointment();
            //userAppointment.setAppointment_coordinatorName("" + appointments.getCoordinatorID());
            userAppointment.setAppointment_id(userAppoint.getId());
            userAppointment.setAppointment_customerName(userAppoint.getName());
            userAppointment.setAppointment_phoneNumber(userAppoint.getPhoneNumber());
            userAppointment.setAppointment_DOB(userAppoint.getDob());
            userAppointment.setAppointment_status(appointments.getStatus());
            userAppointment.setAppointment_note(appointments.getNote());
            userAppointment.setAppointment_meetingTime(appointments.getMeetingTime());
            userAppointment.setAppointment_createdTime(appointments.getCreatedTime());
            listUserAppoinment.add(userAppointment);
        }
        return new ResponseEntity<>(listUserAppoinment, HttpStatus.OK);
    }

    //change Password
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordModel changePasswordModel, @PathVariable("id") int id) throws NoSuchAlgorithmException {
        Optional<User> getCustomer = userService.findUserByID(id);
        if (!getCustomer.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "User not found"), HttpStatus.NOT_FOUND);
        }
        if (!getCustomer.get().getRole().equals("CUSTOMER")) {
            return new ResponseEntity<>(new ApiResponse(true, "User is not allowed"), HttpStatus.NOT_FOUND);
        }
        if(!getCustomer.get().getPassword().equals(toHexString(getSHA(changePasswordModel.getOldPassword())))){
            return new ResponseEntity<>(new ApiResponse(true, "Incorrect current password"), HttpStatus.BAD_REQUEST);
        }
        changePasswordModel.setID(id);
        getCustomer.get().setPassword(toHexString(getSHA(changePasswordModel.getNewPassword())));
        userService.saveUser(getCustomer.get());
        return new ResponseEntity<>(new ApiResponse(true, "Change password successfully!"), HttpStatus.OK);
    }
}
