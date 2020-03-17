package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Appointment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AppointmentService {
    void saveAppointment(Appointment appointment);

    List<Appointment> listAppoinment();

    Optional<Appointment> getAppointmentByID(int id);

    void update(Appointment appointment);


}
