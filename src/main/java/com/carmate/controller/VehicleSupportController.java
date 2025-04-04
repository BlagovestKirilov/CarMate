package com.carmate.controller;

import com.carmate.config.security.RequiresAdmin;
import com.carmate.entity.vehicleSupport.VehicleSupportDTO;
import com.carmate.service.VehicleSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequiresAdmin
    @PostMapping("/save-vehicle-support")
    public ResponseEntity<?> saveVehicleSupports(@RequestBody VehicleSupportDTO vehicleSupportDTO) {
        vehicleSupportService.saveVehicleSupport(vehicleSupportDTO);
        return ResponseEntity.ok().build();
    }

    @RequiresAdmin
    @DeleteMapping("/delete-vehicle-support/{vehicleSupportID}")
    public ResponseEntity<?> deleteVehicleSupport(@PathVariable Long vehicleSupportID) {
        vehicleSupportService.deleteVehicleSupport(vehicleSupportID);
        return ResponseEntity.ok().build();
    }
}
