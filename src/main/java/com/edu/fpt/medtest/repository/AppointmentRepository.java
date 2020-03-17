package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAllByCustomerID(int customerID);
}
