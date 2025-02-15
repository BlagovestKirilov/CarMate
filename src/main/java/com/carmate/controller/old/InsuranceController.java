package com.carmate.controller.old;

import com.carmate.entity.insurance.external.InsuranceResponse;
import com.carmate.service.external.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

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
