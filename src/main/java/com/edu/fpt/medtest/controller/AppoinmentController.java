package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.model.UserAppointmentModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.service.AppointmentService;
import com.edu.fpt.medtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppoinmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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
        List<User> users = userRepository.findAllByRole("COORDINATOR");
        appointment.setNote("");
        appointment.setStatus("pending");
        appointment.setCoordinatorID(users.get(0).getId());
        appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(new ApiResponse(true, "Successfully create appointment"), HttpStatus.OK);
    }

    //appointment detail
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Appointment not found"), HttpStatus.NOT_FOUND);
        } else {
            User userAppoint = new User();
            userAppoint.setId(getAppointment.get().getID());
            List<User> user = userService.getListUser();
            for (User userTracking : user) {
                if (userTracking.getId() == userAppoint.getId()) {
                    userAppoint = userTracking;
                    userAppointmentModel.setAppointment_customerName(userAppoint.getName());
                    userAppointmentModel.setAppointment_phoneNumber(userAppoint.getPhoneNumber());
                    userAppointmentModel.setAppointment_DOB(userAppoint.getDob());
                }
            }
            userAppointmentModel.setAppointment_id(getAppointment.get().getID());
            userAppointmentModel.setAppointment_status(getAppointment.get().getStatus());
            userAppointmentModel.setAppointment_note(getAppointment.get().getNote());
            userAppointmentModel.setAppointment_meetingTime(getAppointment.get().getMeetingTime());
            userAppointmentModel.setAppointment_createdTime(getAppointment.get().getCreatedTime());
        }
        return new ResponseEntity<>(userAppointmentModel, HttpStatus.OK);
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

    @GetMapping(value = "/list/{status}")
    public ResponseEntity<?> getListAppointmentByStatus(@PathVariable("status") String status){
        List<Appointment> lsAppointByStatus = appointmentService.listAppointmentByStatus(status);
        if(lsAppointByStatus.isEmpty()){
            return new ResponseEntity<>(new ApiResponse(true,"No appointment with status" + status),HttpStatus.NOT_FOUND);
        }
        List<UserAppointmentModel> listUserAppoinment = new ArrayList<>();
        for (Appointment appointments : lsAppointByStatus) {
            UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
            Optional<User> userAppoint = userRepository.findById(appointments.getCustomerID());
            userAppointmentModel.setAppointment_id(userAppoint.get().getId());
            userAppointmentModel.setAppointment_customerName(userAppoint.get().getName());
            userAppointmentModel.setAppointment_phoneNumber(userAppoint.get().getPhoneNumber());
            userAppointmentModel.setAppointment_DOB(userAppoint.get().getDob());
            userAppointmentModel.setAppointment_status(appointments.getStatus());
            userAppointmentModel.setAppointment_note(appointments.getNote());
            userAppointmentModel.setAppointment_meetingTime(appointments.getMeetingTime());
            userAppointmentModel.setAppointment_createdTime(appointments.getCreatedTime());
            listUserAppoinment.add(userAppointmentModel);
        }
        return new ResponseEntity<>(listUserAppoinment,HttpStatus.OK);
    }

    /*@GetMapping(value = "/list/listStatus/{listStatus}")
    public  ResponseEntity<?> getListAppointmentByListStatus(@PathVariable("listStatus") Set<String> listStatus){
        List<Appointment> lsAppointmentByStatusList = appointmentService.listAppointmentByListStatus(listStatus);
        return new ResponseEntity<>(lsAppointmentByStatusList,HttpStatus.OK);
    }*/


}
