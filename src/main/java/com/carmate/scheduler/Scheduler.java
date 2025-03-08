package com.carmate.scheduler;

import com.carmate.service.NotificationService;
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
    private final NotificationService notificationService;

    @Autowired
    public Scheduler(VignetteService vignetteService,
                     InsuranceService insuranceService,
                     TechnicalReviewService technicalReviewService,
                     ObligationService obligationService,
                     NotificationService notificationService) {
        this.vignetteService = vignetteService;
        this.insuranceService = insuranceService;
        this.technicalReviewService = technicalReviewService;
        this.obligationService = obligationService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    private void externalServicesChecks() {
        vignetteService.vignetteScheduler();
        insuranceService.insuranceScheduler();
        technicalReviewService.technicalReviewScheduler();
        obligationService.obligationScheduler();
        notificationService.generateNotifications();
    }

    @Scheduled(cron = "0 40 18 * * *")
    private void sendNotifications() {
        notificationService.sendCurrentDateNotification();
    }
}
