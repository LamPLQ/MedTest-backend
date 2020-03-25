package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    List<Notification> lsNotification(int userID);

    void saveNoti(Notification notification);
}
