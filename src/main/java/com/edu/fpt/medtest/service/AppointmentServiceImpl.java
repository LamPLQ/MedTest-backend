package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Appointment;
import com.edu.fpt.medtest.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Appointment getAppointmentByID(String id) {
        //Optional<Appointment> getAppointment = appointmentRepository.findById(id);
        Appointment getAppointment = appointmentRepository.findByID(id);
        return getAppointment;
    }

    @Override
    public void update(Appointment appointment) {
        //Appointment appointmentByID = appointmentRepository.findById(appointment.getID()).get();
        Appointment appointmentByID = appointmentRepository.findByID(appointment.getID());
        appointmentByID.setStatus(appointment.getStatus());
        appointmentRepository.save(appointmentByID);
    }

    //coordinator accept appointment
    @Override
    public void acceptAppointment(Appointment appointment) {
        //Appointment appointmentByID = appointmentRepository.findById(appointment.getID()).get();
        Appointment appointmentByID = appointmentRepository.findByID(appointment.getID());
        appointmentByID.setCoordinatorID(appointment.getCoordinatorID());
        appointmentByID.setStatus(appointment.getStatus());
        appointmentRepository.save(appointmentByID);
    }

    @Override
    public List<Appointment> listAppointmentByStatus(String status) {
        List<Appointment> lsAppointmentByStatus = appointmentRepository.findAllByStatus(status);
        return lsAppointmentByStatus;
    }

    /*@Override
    public List<Appointment> listAppointmentByListStatus(Set<String> status) {
        List<Appointment> lsAppointmentByStatusList = appointmentRepository.findAllByStatusList(status);
        return lsAppointmentByStatusList;
    }*/


}
