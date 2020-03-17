package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> listAppoinment() {
        List<Appointment> getAppointmentList = (List<Appointment>) appointmentRepository.findAll();
        return getAppointmentList;
    }

    @Override
    public Optional<Appointment> getAppointmentByID(int id) {
        Optional<Appointment> getAppointment = appointmentRepository.findById(id);
        return getAppointment;
    }

    @Override
    public void update(Appointment appointment) {
        Appointment appointmentByID = appointmentRepository.findById(appointment.getID()).get();
        appointmentByID.setStatus(appointment.getStatus());
        appointmentRepository.save(appointmentByID);
    }


}
