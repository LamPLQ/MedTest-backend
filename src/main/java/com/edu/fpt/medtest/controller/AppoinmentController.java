package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.UserAppointment;
import com.edu.fpt.medtest.service.AppointmentService;
import com.edu.fpt.medtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppoinmentController {

    @Autowired
    AppointmentService appointmentService;

    @Autowired
    UserService userService;

    //list appointment
    @GetMapping("/list")
    public ResponseEntity<?> listAppoinment() {
        List<Appointment> listAppointment = appointmentService.listAppoinment();
        if (listAppointment.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No appointment"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listAppointment, HttpStatus.OK);
    }

    //create new appointment
    @PostMapping("/create")
    public ResponseEntity<?> createNewAppointment(@RequestBody Appointment appointment) {
        appointment.setNote("");
        appointment.setStatus("pending");
        appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create appointment"), HttpStatus.OK);
    }

    //appointment detail
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        UserAppointment userAppointment = new UserAppointment();
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        } else {
            User userAppoint = new User();
            userAppoint.setId(getAppointment.get().getID());
            List<User> user = userService.getListUser();
            for (User userTracking : user) {
                if (userTracking.getId() == userAppoint.getId()) {
                    userAppoint = userTracking;
                    userAppointment.setAppointment_customerName(userAppoint.getName());
                    userAppointment.setAppointment_phoneNumber(userAppoint.getPhoneNumber());
                    userAppointment.setAppointment_DOB(userAppoint.getDob());
                }
            }
            userAppointment.setAppointment_status(getAppointment.get().getStatus());
            userAppointment.setAppointment_note(getAppointment.get().getNote());
            userAppointment.setAppointment_meetingTime(getAppointment.get().getMeetingTime());
            userAppointment.setAppointment_createdTime(getAppointment.get().getCreatedTime());
        }
        return new ResponseEntity<>(userAppointment, HttpStatus.OK);
    }

    //update appointment
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointment, @PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        }
        appointment.setID(id);
        appointmentService.update(appointment);
        return new ResponseEntity<>(new ApiResponse(true, "Update appointment successfully"), HttpStatus.OK);
    }


}
