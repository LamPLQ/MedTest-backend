package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface AppointmentService {
    void saveAppointment(Appointment appointment);

    List<Appointment> listAppoinment();

    Appointment getAppointmentByID(String id);

    void update(Appointment appointment);

    void acceptAppointment(Appointment appointment);

    void rejectAppointment(Appointment appointment);

    List<Appointment> listAppointmentByStatus(String status);

    List<Appointment> listAllAppointmentByCreatedTimeDesc();

    //List<Appointment> listAppointmentByListStatus(Set<String> status);

}
