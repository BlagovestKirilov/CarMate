package com.carmate.controller;

import com.carmate.config.security.RequiresAdmin;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.service.VehicleService;
import com.carmate.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {

    private final VehicleService vehicleService;
    private final PdfService pdfService;

    @Autowired
    public AdminController(VehicleService vehicleService, PdfService pdfService) {
        this.vehicleService = vehicleService;
        this.pdfService = pdfService;
    }

    @RequiresAdmin
    @PostMapping(value = "/generate-pdf")
    public ResponseEntity<?> generateTripSheetPdf(@RequestBody List<Long> tripSheetIds) {
        try {
            pdfService.generateTripSheetPdf(tripSheetIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @RequiresAdmin
    @GetMapping("/get-trip-sheet")
    public ResponseEntity<List<TripSheetDTO>> getTripSheet() {
        List<TripSheetDTO> tripSheetDTOS = vehicleService.getTripSheets();
        return ResponseEntity.ok(tripSheetDTOS);
    }

    @RequiresAdmin
    @DeleteMapping("/delete-trip-sheet/{tripSheetID}")
    public ResponseEntity<Void> deleteTripSheet(@PathVariable Long tripSheetID) {
        vehicleService.deleteTripSheet(tripSheetID);
        return ResponseEntity.ok().build();
    }
}
