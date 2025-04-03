package com.carmate.controller;

import com.carmate.entity.vehicleSupport.VehicleSupportDTO;
import com.carmate.service.VehicleSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VehicleSupportController {

    private final VehicleSupportService vehicleSupportService;

    @Autowired
    public VehicleSupportController(VehicleSupportService vehicleSupportService) {
        this.vehicleSupportService = vehicleSupportService;
    }

    @GetMapping("/get-vehicle-support")
    public ResponseEntity<List<VehicleSupportDTO>> getVehicleSupports() {
        List<VehicleSupportDTO> vehicleSupportDTOS = vehicleSupportService.getVehicleSupportDTOs();
        return ResponseEntity.ok(vehicleSupportDTOS);
    }

    @PostMapping("/save-vehicle-support")
    public ResponseEntity<?> saveVehicleSupports(@RequestBody VehicleSupportDTO vehicleSupportDTO) {
        vehicleSupportService.saveVehicleSupport(vehicleSupportDTO);
        return ResponseEntity.ok().build();
    }
}
