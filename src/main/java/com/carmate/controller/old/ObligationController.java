package com.carmate.controller.old;

import com.carmate.entity.obligation.ObligationResponseResult;
import com.carmate.service.external.ObligationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ObligationController {
    @Autowired
    private ObligationServiceImpl obligationService;

    @GetMapping("/check-obligation/{plateNumber}/{egn}")
    public ResponseEntity<ObligationResponseResult> checkVignette(@PathVariable String plateNumber, @PathVariable String egn) {
        ObligationResponseResult response = obligationService.obligationCheck(plateNumber, egn);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
