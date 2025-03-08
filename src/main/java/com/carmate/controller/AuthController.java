package com.carmate.controller;

import com.carmate.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String token = authService.register(request.get("email"), request.get("password"), request.get("accountName"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String token = authService.login(request.get("email"), request.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/confirmRegistration")
    public ResponseEntity<?> confirmRegistration(@RequestBody Map<String, String> request) {
        authService.confirmRegistration(request.get("email"), request.get("password"), request.get("code"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().body("Logged out successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String token = authService.forgotPassword(request.get("email"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/confirm-forgot-password") //confirmation
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request,
                                           @RequestHeader(name = "Authorization") String authorizationHeader) {
        String token = authService.confirmForgotPassword(request.get("email"), request.get("code"), authorizationHeader);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                            @RequestHeader(name = "Authorization") String authorizationHeader) {
        authService.changePassword(request.get("newPassword"), authorizationHeader);
        return ResponseEntity.ok().build();
    }
}