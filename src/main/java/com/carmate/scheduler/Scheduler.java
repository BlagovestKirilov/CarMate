package com.carmate.scheduler;

import com.carmate.service.external.InsuranceService;
import com.carmate.service.external.ObligationService;
import com.carmate.service.external.TechnicalReviewService;
import com.carmate.service.external.VignetteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

    private final VignetteService vignetteService;
    private final InsuranceService insuranceService;
    private final TechnicalReviewService technicalReviewService;
    private final ObligationService obligationService;

    @Autowired
    public Scheduler(VignetteService vignetteService,
                     InsuranceService insuranceService,
                     TechnicalReviewService technicalReviewService,
                     ObligationService obligationService) {
        this.vignetteService = vignetteService;
        this.insuranceService = insuranceService;
        this.technicalReviewService = technicalReviewService;
        this.obligationService = obligationService;
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void schedulerChecks(){
        vignetteService.vignetteScheduler();
        insuranceService.insuranceScheduler();
        technicalReviewService.technicalReviewScheduler();
        obligationService.obligationScheduler();
    }

}
