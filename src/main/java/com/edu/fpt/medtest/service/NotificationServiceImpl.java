package com.edu.fpt.medtest.service;

import com.edu.fpt.medtest.entity.Notification;
import com.edu.fpt.medtest.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> lsNotification(int userID) {
        List<Notification> lsNoti = notificationRepository.getAllByUserIDOrderByCreatedTimeDesc(userID);
        return lsNoti;
    }

    @Override
    public void saveNoti(Notification notification) {
        notificationRepository.save(notification);
    }
}
