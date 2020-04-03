package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.Notification;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.UserAppointmentModel;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.AppointmentService;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.service.UserService;
import com.edu.fpt.medtest.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    //list appointment
    @GetMapping("/list")
    public ResponseEntity<?> listAppoinment() {
        List<Appointment> listAppointment = appointmentService.listAppoinment();
        if (listAppointment.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có lịch hẹn nào!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(listAppointment, HttpStatus.OK);
    }

    //create new appointment
    @PostMapping("/create")
    public ResponseEntity<?> createNewAppointment(@RequestBody Appointment appointment) {
        Optional<User> userCreateAppointment = userRepository.getUserByIdAndRole(appointment.getCustomerID(),"CUSTOMER");
        if(!userCreateAppointment.isPresent()){
            return new ResponseEntity<>(new ApiResponse(true,"Người dùng không được phép truy cập tính năng này"),HttpStatus.OK);
        }
        appointment.setNote("");
        appointment.setStatus("pending");
        appointment.setCoordinatorID(1);
        appointmentService.saveAppointment(appointment);
        return new ResponseEntity<>(new ApiResponse(true, "Tạo lịch hẹn thành công!"), HttpStatus.OK);
    }

    //appointment detail
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Lịch hẹn không tồn tại"), HttpStatus.OK);
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

    //customer update appointment (canceled)
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointment, @PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
        }
        appointment.setID(id);
        appointmentService.update(appointment);
        //Notification
        Appointment notiAppointment = appointmentService.getAppointmentByID(id).get();
        Notification notification = new Notification();
        notification.setAppointmentID(notiAppointment.getID());
        notification.setType("APPOINTMENT");
        notification.setIsRead(0);
        notification.setRequestID(1);
        notification.setUserID(notiAppointment.getCustomerID());
        notification.setMessage("Khách hàng " + userRepository.findById(notiAppointment.getCustomerID()).get().getName()
                + " đã huỷ cuộc hẹn tại phòng khám lúc " + notiAppointment.getMeetingTime());
        notificationService.saveNoti(notification);
        return new ResponseEntity<>(new ApiResponse(true, "Đã huỷ cuộc hẹn thành công."), HttpStatus.OK);
    }

    //coordinator accepted appointment
    @PutMapping(value = "/accept/{id}")
    public ResponseEntity<?> updateAppointmentByCoordinator(@RequestBody Appointment appointment, @PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
        }
        if(getAppointment.get().getStatus().equals("canceled")){
            return new ResponseEntity<>(new ApiResponse(true,"Cuộc hẹn đã bị huỷ!"), HttpStatus.OK);
        }
        appointment.setID(id);
        appointmentService.acceptAppointment(appointment);
        //Notification
        Appointment notiAppointment = appointmentService.getAppointmentByID(id).get();
        Notification notification = new Notification();
        notification.setAppointmentID(notiAppointment.getID());
        notification.setType("APPOINTMENT");
        notification.setIsRead(0);
        notification.setRequestID(1);
        notification.setUserID(notiAppointment.getCustomerID());
        notification.setMessage("Cuộc hẹn của bạn lúc " + notiAppointment.getMeetingTime() +
                " đã được xác nhận bởi điểu phối viên " + userRepository.findById(notiAppointment.getCoordinatorID()).get().getName());
        notificationService.saveNoti(notification);
        return new ResponseEntity<>(new ApiResponse(true, "Xác nhận đặt cuộc hẹn thành công!"), HttpStatus.OK);
    }

    //coordinator cancel appointment
    @PutMapping(value = "/cancel/{id}")
    public ResponseEntity<?> cancelAppointmentByCoordinator(@RequestBody Appointment appointment, @PathVariable("id") int id) {
        Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
        if (!getAppointment.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
        }
        if(getAppointment.get().getStatus().equals("canceled")){
            return new ResponseEntity<>(new ApiResponse(true,"Cuộc hẹn đã bị huỷ!"), HttpStatus.OK);
        }
        appointment.setID(id);
        appointmentService.acceptAppointment(appointment);
        //Notification
        Appointment notiAppointment = appointmentService.getAppointmentByID(id).get();
        Notification notification = new Notification();
        notification.setAppointmentID(notiAppointment.getID());
        notification.setType("APPOINTMENT");
        notification.setIsRead(0);
        notification.setRequestID(1);
        notification.setUserID(notiAppointment.getCustomerID());
        notification.setMessage("Cuộc hẹn của bạn lúc " + notiAppointment.getMeetingTime() +
                " bị huỷ bởi điểu phối viên " + userRepository.findById(notiAppointment.getCoordinatorID()).get().getName());
        notificationService.saveNoti(notification);
        return new ResponseEntity<>(new ApiResponse(true, "Xác nhận huỷ cuộc hẹn thành công!"), HttpStatus.OK);
    }


    @GetMapping(value = "/list/{status}")
    public ResponseEntity<?> getListAppointmentByStatus(@PathVariable("status") String status) {
        List<Appointment> lsAppointByStatus = appointmentService.listAppointmentByStatus(status);
        String vnStatus = null;
        if(status.equals("pending")){
            vnStatus = "ĐỢI XÁC NHẬN ";
        }else if(status.equals("accepted")){
            vnStatus = "ĐÃ ĐƯỢC XÁC NHẬN";
        }else{
            vnStatus = "ĐƠN ĐÃ HUỶ";
        }
        if (lsAppointByStatus.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(true, "Không có lịch hẹn ở trạng thái " + vnStatus), HttpStatus.OK);
        }
        List<UserAppointmentModel> listUserAppoinment = new ArrayList<>();
        for (Appointment appointments : lsAppointByStatus) {
            UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
            Optional<User> userAppoint = userRepository.findById(appointments.getCustomerID());
            userAppointmentModel.setAppointment_id(appointments.getID());
            userAppointmentModel.setAppointment_customerName(userAppoint.get().getName());
            userAppointmentModel.setAppointment_phoneNumber(userAppoint.get().getPhoneNumber());
            userAppointmentModel.setAppointment_DOB(userAppoint.get().getDob());
            userAppointmentModel.setAppointment_status(appointments.getStatus());
            userAppointmentModel.setAppointment_note(appointments.getNote());
            userAppointmentModel.setAppointment_meetingTime(appointments.getMeetingTime());
            userAppointmentModel.setAppointment_createdTime(appointments.getCreatedTime());
            listUserAppoinment.add(userAppointmentModel);
        }
        return new ResponseEntity<>(listUserAppoinment, HttpStatus.OK);
    }

}
