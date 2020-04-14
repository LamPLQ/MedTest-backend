package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.model.AppointmentModelInput;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentModelRepository extends JpaRepository<AppointmentModelInput, Integer> {
}
