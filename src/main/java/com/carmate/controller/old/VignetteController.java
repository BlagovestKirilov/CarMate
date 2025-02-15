package com.carmate.controller.old;

import com.carmate.entity.vignette.external.VignetteResponse;
import com.carmate.service.external.VignetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VignetteController {

    @Autowired
    private VignetteService vignetteCheckService;

    @GetMapping("/check-vignette/{plateNumber}")
    public ResponseEntity<VignetteResponse> checkVignette(@PathVariable String plateNumber) {
        VignetteResponse response = vignetteCheckService.vignetteCheck(plateNumber);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
