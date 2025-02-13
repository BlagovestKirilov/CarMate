package com.carmate.controller.old;

import com.carmate.entity.technicalReview.TechnicalReviewResponse;
import com.carmate.service.external.TechnicalReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TechnicalReviewController {

    @Autowired
    private TechnicalReviewService technicalReviewService;

    @GetMapping("/check-technical-review/{plateNumber}")
    public ResponseEntity<TechnicalReviewResponse> checkTechnicalReview(@PathVariable String plateNumber) {
        TechnicalReviewResponse response =  technicalReviewService.technicalReviewCheck(plateNumber);

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
