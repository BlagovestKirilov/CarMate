package com.carmate.controller;

import com.carmate.config.security.RequiresAdmin;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.service.CarService;
import com.carmate.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminController {

    private final CarService carService;
    private final PdfService pdfService;

    @Autowired
    public AdminController(CarService carService, PdfService pdfService) {
        this.carService = carService;
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
    @GetMapping("/get-trip-sheet/{carID}")
    public ResponseEntity<List<TripSheetDTO>> getTripSheet(@PathVariable Long carID) {
        List<TripSheetDTO> tripSheetDTOS = carService.getTripSheets(carID);
        return ResponseEntity.ok(tripSheetDTOS);
    }

    @RequiresAdmin
    @GetMapping("/get-trip-sheet-admin")
    public ResponseEntity<List<TripSheetDTO>> getTripSheetAdmin() {
        List<TripSheetDTO> tripSheetDTOS = carService.getTripSheetsAdmin();
        return ResponseEntity.ok(tripSheetDTOS);
    }

    @RequiresAdmin
    @DeleteMapping("/delete-trip-sheet/{tripSheetID}")
    public ResponseEntity<Void> deleteTripSheet(@PathVariable Long tripSheetID) {
        carService.deleteTripSheet(tripSheetID);
        return ResponseEntity.ok().build();
    }
}
