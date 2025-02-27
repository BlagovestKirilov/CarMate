package com.carmate.controller;

import com.carmate.entity.notification.Notification;
import com.carmate.entity.notification.NotificationDTO;
import com.carmate.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

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

    @PostMapping("/save-push-token")
    public String saveFCMToken(@RequestBody Map<String, String> requestBody) {
        String fcmToken = requestBody.get("token");
        notificationService.saveFCMToken(fcmToken);
        return "FCM token saved successfully.";
    }

    // Endpoint to send a test notification
    @GetMapping("/send")
    public String sendTestNotification() {
        String title = "Test Notification";
        String body = "This is a test notification from Spring Boot!";

        notificationService.sendNotification(title, body);
        return "Notification sent!";
    }

    @PostMapping("/change-language")
    public ResponseEntity<String> changeLanguage(@RequestBody Map<String, String> request) {
        String newLanguage = request.get("language");

        if (newLanguage == null || newLanguage.isEmpty()) {
            return ResponseEntity.badRequest().body("Language parameter is missing");
        }

        notificationService.changeAccountLanguage(newLanguage);

        return ResponseEntity.ok("Language changed to: " + newLanguage);
    }
}
