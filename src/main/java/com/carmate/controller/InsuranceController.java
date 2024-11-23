package com.carmate.controller;

import com.carmate.entity.insurance.InsuranceResponse;
import com.carmate.service.external.InsuranceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InsuranceController {

    @Autowired
    private InsuranceServiceImpl insuranceService;

    @GetMapping("/check-insurance/{plateNumber}")
    public ResponseEntity<InsuranceResponse> checkInsurance(@PathVariable String plateNumber) {
        InsuranceResponse response =  insuranceService.insuranceCheck(plateNumber);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
