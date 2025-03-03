package com.carmate.controller;

import com.carmate.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping(value = "/generate-pdf")
    public ResponseEntity<?> generateTripSheetPdf(@RequestBody List<Long> tripSheetIds) {
        try {
            pdfService.generateTripSheetPdf(tripSheetIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}