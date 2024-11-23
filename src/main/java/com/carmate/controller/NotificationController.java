package com.carmate.controller;

import com.carmate.entity.notification.Notification;
import com.carmate.service.NotificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private NotificationServiceImpl notificationService;

    @GetMapping("/get-notification/{deviceID}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String deviceID) {
        List<Notification> resultNotifications = notificationService.getAllNotificationsByDateAndDeviceId(deviceID);
        return ResponseEntity.ok(resultNotifications);
    }

    @GetMapping("/get-all-notification/{deviceID}")
    public ResponseEntity<List<Notification>> getOldNotifications(@PathVariable String deviceID) {
        List<Notification> resultNotifications = notificationService.getAllNotificationsBydDeviceId(deviceID);
        return ResponseEntity.ok(resultNotifications);
    }

    @GetMapping("/generate-notification")
    public void generateNotifications() {
        notificationService.generateNotifications();
    }
}
