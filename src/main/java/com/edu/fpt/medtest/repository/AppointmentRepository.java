package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findAllByCustomerID(int customerID);

    List<Appointment> findAllByStatus(String status);

    Appointment findByID(String ID);

    boolean existsByID(String ID);

    /*@Query(value = "SELECT appointment.ID FROM appointment  WHERE appointment.Status IN (:status)")
    List<Appointment> findAllByStatusList(@Param("status") Set<String> statusList);*/
}
