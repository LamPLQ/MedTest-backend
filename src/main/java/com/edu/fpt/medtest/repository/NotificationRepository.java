package com.edu.fpt.medtest.repository;

import com.edu.fpt.medtest.entity.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {
    List<Notification> getAllByUserID(int userID);
}
