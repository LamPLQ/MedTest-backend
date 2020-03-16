package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.District;
import com.edu.fpt.medtest.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppoinmentController {

    @Autowired
    AppointmentService appointmentService;

    @Autowired

    @GetMapping("/list")
    public ResponseEntity<?> listAppoinment() {
        List<Appointment> listAppointment = appointmentService.listAppoinment();
        if (listAppointment.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "No appointment"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listAppointment, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewAppointment(Appointment appointment) {

        return null;
    }
}
