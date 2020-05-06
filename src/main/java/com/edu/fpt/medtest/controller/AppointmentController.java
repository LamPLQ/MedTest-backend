package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.entity.Notification;
import com.edu.fpt.medtest.entity.User;
import com.edu.fpt.medtest.model.AppointmentModelInput;
import com.edu.fpt.medtest.model.UserAppointmentModel;
import com.edu.fpt.medtest.repository.AppointmentModelRepository;
import com.edu.fpt.medtest.repository.AppointmentRepository;
import com.edu.fpt.medtest.repository.UserRepository;
import com.edu.fpt.medtest.service.AppointmentService;
import com.edu.fpt.medtest.service.NotificationService;
import com.edu.fpt.medtest.utils.ApiResponse;
import com.edu.fpt.medtest.utils.GetRandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppointmentModelRepository appointmentModelRepository;

    //list appointment
    @GetMapping("/list")
    public ResponseEntity<?> listAppoinment() {
        try {
            List<Appointment> listAppointment = appointmentService.listAppoinment();
            if (listAppointment.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có lịch hẹn nào trong hệ thống!"), HttpStatus.OK);
            }
            List<UserAppointmentModel> returnAppointmentList = new ArrayList<>();
            for (Appointment appointment : listAppointment.subList(1, listAppointment.size())) {
                String id = appointment.getID();
                Appointment appointmentExcuting = appointmentService.getAppointmentByID(id);
                UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
                userAppointmentModel.setAppointment_customerName(userRepository.findById(appointmentExcuting.getCustomerID()).get().getName());
                userAppointmentModel.setAppointment_phoneNumber(userRepository.findById(appointmentExcuting.getCustomerID()).get().getPhoneNumber());
                userAppointmentModel.setAppointment_DOB(userRepository.findById(appointmentExcuting.getCustomerID()).get().getDob());
                userAppointmentModel.setAppointment_id(appointmentExcuting.getID());
                userAppointmentModel.setAppointment_status(appointmentExcuting.getStatus());
                userAppointmentModel.setAppointment_note(appointmentExcuting.getNote());
                userAppointmentModel.setAppointment_meetingTime(appointmentExcuting.getMeetingTime());
                //=====================//
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf2.format(appointmentExcuting.getCreatedTime());
                String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//
                userAppointmentModel.setAppointment_createdTime(createdTime);
                returnAppointmentList.add(userAppointmentModel);
            }
            if (returnAppointmentList.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(true, "Không có lịch hẹn nào hiện tại!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(returnAppointmentList, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //create new appointment (pending)
    @PostMapping("/create")
    public ResponseEntity<?> createNewAppointment(@RequestBody Appointment appointment) {
        try {
            try {
                if (appointment.getMeetingTime().toString().isEmpty() || appointment.getCustomerID() == 0) {
                    return new ResponseEntity<>(new ApiResponse(false, "Người dùng cần nhập đủ thông tin trước khi tạo cuộc hẹn mới!"), HttpStatus.OK);
                }
            } catch (NullPointerException ex) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng cần nhập đủ thông tin trước khi tạo cuộc hẹn mới!"), HttpStatus.OK);
            }
            Optional<User> createUser = userRepository.findById(appointment.getCustomerID());
            if (!createUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại"), HttpStatus.OK);
            }
            if (createUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            Optional<User> userCreateAppointment = userRepository.getUserByIdAndRole(appointment.getCustomerID(), "CUSTOMER");
            if (!userCreateAppointment.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(true, "Người dùng không được phép truy cập tính năng này"), HttpStatus.OK);
            }
            //////////save temp Appointment
            AppointmentModelInput appointmentModelInput = new AppointmentModelInput();
            appointmentModelInput.setTemCustomerID(appointment.getCustomerID());
            appointmentModelInput.setTemMeetingTime(appointment.getMeetingTime());
            appointmentModelRepository.save(appointmentModelInput);
            //////////
            String appointmentID;
            do {
                appointmentID = GetRandomString.getAlphaNumericStringUpper(6);
            } while (appointmentRepository.existsByID(appointmentID));
            //
            appointment.setID(appointmentID);
            appointment.setNote("");
            appointment.setStatus("pending");
            appointment.setCoordinatorID(1);
            appointment.setCreatedTime(appointmentModelInput.getTempCreatedTime());
            appointmentService.saveAppointment(appointment);
            //
            UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
            userAppointmentModel.setAppointment_id(appointment.getID());
            userAppointmentModel.setAppointment_customerName(userRepository.findById(appointment.getCustomerID()).get().getName());
            userAppointmentModel.setAppointment_phoneNumber(userRepository.findById(appointment.getCustomerID()).get().getPhoneNumber());
            userAppointmentModel.setAppointment_DOB(userRepository.findById(appointment.getCustomerID()).get().getDob());
            userAppointmentModel.setAppointment_status(appointment.getStatus());
            userAppointmentModel.setAppointment_note(appointment.getNote());
            userAppointmentModel.setAppointment_meetingTime(appointment.getMeetingTime());
            //=====================//
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String displayCreatedTest = sdf2.format(appointment.getCreatedTime());
            String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
            //=====================//
            userAppointmentModel.setAppointment_createdTime(createdTime);
            return new ResponseEntity<>(userAppointmentModel, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //appointment detail
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable("id") String id) {
        try {
            Appointment appointmentExecuting = appointmentService.getAppointmentByID(id);
            UserAppointmentModel userAppointmentModel = new UserAppointmentModel();
            if (appointmentExecuting == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Lịch hẹn không tồn tại"), HttpStatus.OK);
            } else {
                userAppointmentModel.setAppointment_customerName(userRepository.findById(appointmentExecuting.getCustomerID()).get().getName());
                userAppointmentModel.setAppointment_phoneNumber(userRepository.findById(appointmentExecuting.getCustomerID()).get().getPhoneNumber());
                userAppointmentModel.setAppointment_DOB(userRepository.findById(appointmentExecuting.getCustomerID()).get().getDob());
                userAppointmentModel.setAppointment_id(appointmentExecuting.getID());
                userAppointmentModel.setAppointment_status(appointmentExecuting.getStatus());
                userAppointmentModel.setAppointment_note(appointmentExecuting.getNote());
                userAppointmentModel.setAppointment_meetingTime(appointmentExecuting.getMeetingTime());
                //=====================//
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf2.format(appointmentExecuting.getCreatedTime());
                String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//
                userAppointmentModel.setAppointment_createdTime(createdTime);
            }
            return new ResponseEntity<>(userAppointmentModel, HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //customer update appointment (canceled)
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointment, @PathVariable("id") String id) {
        try {
            try {
                if(!appointment.getStatus().equals("canceled")){
                    return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
                }
            }catch (Exception e){
                return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
            }
            Appointment appointmentExecuting = appointmentService.getAppointmentByID(id);
            Optional<User> createUser = userRepository.findById(appointmentExecuting.getCustomerID());
            if (!createUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không tồn tại!"), HttpStatus.OK);
            }
            if (createUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            if (appointmentExecuting.getStatus().equals(appointment.getStatus())) {
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã ở trạng thái bị huỷ!"), HttpStatus.OK);
            }
            if (appointmentExecuting.getStatus().equals("rejected") || appointmentExecuting.getStatus().equals("accepted")){
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã bị từ chối!"), HttpStatus.OK);
            }
            if (appointmentExecuting == null) {
                return new ResponseEntity<>(new ApiResponse(true, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
            }
            appointment.setID(id);
            appointmentService.update(appointment);
            //Notification
            Appointment notiAppointment = appointmentExecuting;
            Notification notification = new Notification();
            notification.setAppointmentID(notiAppointment.getID());
            notification.setType("APPOINTMENT");
            notification.setIsRead(0);
            notification.setRequestID("000000");
            notification.setUserID(notiAppointment.getCustomerID());
            SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String displayTime = sdf3.format(notiAppointment.getMeetingTime());
            notification.setMessage("Khách hàng " + userRepository.findById(notiAppointment.getCustomerID()).get().getName()
                    + " đã huỷ cuộc hẹn mã " + notiAppointment.getID() + ". Trạng thái cuộc hẹn: Đã huỷ.");
            notificationService.saveNoti(notification);
            return new ResponseEntity<>(new ApiResponse(true, "Đã huỷ cuộc hẹn thành công."), HttpStatus.OK);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //coordinator accepted appointment (accepted)
    @PostMapping(value = "/accept/{id}")
    public ResponseEntity<?> updateAppointmentByCoordinator(@RequestBody Appointment appointment, @PathVariable("id") String id) {
        try {
            try {
                if(!appointment.getStatus().equals("accepted")){
                    return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
                }
            }catch (Exception e){
                return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
            }
            Optional<User> processUser = userRepository.findById(appointment.getCoordinatorID());
            if (!processUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            if(!(processUser.get().getRole().equals("COORDINATOR")||processUser.get().getRole().equals("ADMIN"))){
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không thực hiện được chức năng này!"), HttpStatus.OK);
            }
            if (processUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            Appointment appointmentExecuting = appointmentService.getAppointmentByID(id);
            if (appointmentExecuting.getStatus().equals(appointment.getStatus())) {
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã ở trạng thái đã nhận!"), HttpStatus.OK);
            }
            if (appointmentExecuting == null) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
            }
            if (appointmentExecuting.getStatus().equals("canceled") || appointmentExecuting.getStatus().equals("rejected")) {
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã bị huỷ!"), HttpStatus.OK);
            }
            appointment.setID(id);
            appointmentService.acceptAppointment(appointment);
            //Notification
            Appointment notiAppointment = appointmentService.getAppointmentByID(id);
            Notification notification = new Notification();
            notification.setAppointmentID(notiAppointment.getID());
            notification.setType("APPOINTMENT");
            notification.setIsRead(0);
            notification.setRequestID("000000");
            notification.setUserID(notiAppointment.getCustomerID());
            notification.setMessage("Cuộc hẹn mã " + notiAppointment.getID() + " đã được xác nhận bởi điểu phối viên " +
                    userRepository.findById(notiAppointment.getCoordinatorID()).get().getName() + ". Trạng thái: Đã nhận đơn.");
            notificationService.saveNoti(notification);
            return new ResponseEntity<>(new ApiResponse(true, "Xác nhận đặt cuộc hẹn thành công!"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }

    //coordinator reject appointment (rejected)
    @PostMapping(value = "/reject/{id}")
    public ResponseEntity<?> cancelAppointmentByCoordinator(@RequestBody Appointment appointment, @PathVariable("id") String id) {
        try {
            try {
                if(!appointment.getStatus().equals("rejected")){
                    return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
                }
            }catch (Exception e){
                return new ResponseEntity<>(new ApiResponse(false,"Không xác định được yêu cầu!"), HttpStatus.OK);
            }
            //Optional<Appointment> getAppointment = appointmentService.getAppointmentByID(id);
            Optional<User> processUser = userRepository.findById(appointment.getCoordinatorID());
            if (!processUser.isPresent()) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy người dùng!"), HttpStatus.OK);
            }
            if(!(processUser.get().getRole().equals("COORDINATOR")||processUser.get().getRole().equals("ADMIN"))){
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng không thực hiện được chức năng này!"), HttpStatus.OK);
            }
            if (processUser.get().getActive() == 0) {
                return new ResponseEntity<>(new ApiResponse(false, "Người dùng hiện tại đang bị khoá! Vui lòng liên hệ tới phòng khám để xử lý!"), HttpStatus.OK);
            }
            Appointment appointmentExecuting = appointmentService.getAppointmentByID(id);
            if (appointmentExecuting.getStatus().equals(appointment.getStatus())) {
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã ở trạng thái bị từ chối!"), HttpStatus.OK);
            }
            if (appointmentExecuting == null) {
                return new ResponseEntity<>(new ApiResponse(false, "Không tìm thấy cuộc hẹn!"), HttpStatus.OK);
            }
            if (appointmentExecuting.getStatus().equals("canceled") || appointmentExecuting.getStatus().equals("rejected")) {
                return new ResponseEntity<>(new ApiResponse(false, "Cuộc hẹn đã bị huỷ!"), HttpStatus.OK);
            }
            if (appointmentExecuting.getStatus().equals("accepted") || appointmentExecuting.getStatus().equals("rejected")) {
                return new ResponseEntity<>(new ApiResponse(false, "Không thể huỷ cuộc hẹn đã được chấp nhận!"), HttpStatus.OK);
            }
            appointment.setID(id);
            appointmentService.rejectAppointment(appointment);
            //Notification
            Appointment notiAppointment = appointmentService.getAppointmentByID(id);
            Notification notification = new Notification();
            notification.setAppointmentID(notiAppointment.getID());
            notification.setType("APPOINTMENT");
            notification.setIsRead(0);
            notification.setRequestID("000000");
            notification.setUserID(notiAppointment.getCustomerID());
            notification.setMessage("Cuộc hẹn mã " + notiAppointment.getID() +
                    " bị huỷ bởi điểu phối viên " + userRepository.findById(notiAppointment.getCoordinatorID()).get().getName()
                    + " do nguyên nhân: " + notiAppointment.getNote() + ". Trạng thái lịch hẹn: Đơn đã huỷ.");
            notificationService.saveNoti(notification);
            return new ResponseEntity<>(new ApiResponse(true, "Xác nhận điều phối viên huỷ cuộc hẹn thành công!"), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }


    @GetMapping(value = "/list/{status}")
    public ResponseEntity<?> getListAppointmentByStatus(@PathVariable("status") String status) {
        try {
            List<Appointment> lsAppointByStatus = appointmentService.listAppointmentByStatus(status);
            String vnStatus = null;
            if (status.equals("pending")) {
                vnStatus = "ĐỢI XÁC NHẬN ";
            } else if (status.equals("accepted")) {
                vnStatus = "ĐÃ ĐƯỢC XÁC NHẬN";
            } else {
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
                //=====================//
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String displayCreatedTest = sdf2.format(appointments.getCreatedTime());
                String createdTime = displayCreatedTest.substring(0, 10) + "T" + displayCreatedTest.substring(11) + ".000+0000";
                //=====================//
                userAppointmentModel.setAppointment_createdTime(createdTime);
                listUserAppoinment.add(userAppointmentModel);
            }
            return new ResponseEntity<>(listUserAppoinment, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse(false, "Hệ thống đang xử lý. Vui lòng tải lại!"), HttpStatus.OK);
        }
    }
}
