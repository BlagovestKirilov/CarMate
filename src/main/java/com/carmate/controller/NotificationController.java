package com.carmate.controller;

import com.carmate.entity.notification.Notification;
import com.carmate.entity.notification.NotificationDTO;
import com.carmate.service.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private NotificationServiceImpl notificationService;

    @GetMapping("/get-notification")
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        List<NotificationDTO> resultNotifications = notificationService.getAllNotificationsByDateAndAccount();
        return ResponseEntity.ok(resultNotifications);
    }

    @GetMapping("/get-all-notification")
    public ResponseEntity<List<Notification>> getOldNotifications() {
        List<Notification> resultNotifications = notificationService.getAllNotificationsByAccount();
        return ResponseEntity.ok(resultNotifications);
    }

    @GetMapping("/generate-notification")
    public void generateNotifications() {
        notificationService.generateNotifications();
    }
}
