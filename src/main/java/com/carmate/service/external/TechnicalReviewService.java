package com.carmate.service.external;

import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.technicalReview.TechnicalReview;
import com.carmate.entity.technicalReview.external.TechnicalReviewResponse;
import com.carmate.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TechnicalReviewService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public TechnicalReviewService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    private static final String TECHNICAL_REVIEW_ENDPOINT = "https://myve.bg/api/get/gtp";

    private static final Logger LOGGER = LoggerFactory.getLogger(TechnicalReviewService.class);

    @Transactional
    public void technicalReviewCheck(Vehicle vehicle) {
        TechnicalReviewResponse technicalReviewResponse = technicalReviewCheckExternal(vehicle.getPlateNumber());
        boolean isValid = false;
        Date expiryDate = null;

        Pattern validPattern = Pattern.compile("Има валиден периодичен технически преглед!");
        Matcher validMatcher = validPattern.matcher(technicalReviewResponse.getData());

        if (validMatcher.find()) {
            isValid = true;

            Pattern datePattern = Pattern.compile("валиден до\\s*(\\d{2}\\.\\d{2}\\.\\d{4})");
            Matcher dateMatcher = datePattern.matcher(technicalReviewResponse.getData());

            if (dateMatcher.find()) {
                String dateString = dateMatcher.group(1);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    expiryDate = dateFormat.parse(dateString);
                } catch (ParseException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        TechnicalReview technicalReview = vehicle.getTechnicalReview() != null ? vehicle.getTechnicalReview() : new TechnicalReview();
        technicalReview.setIsActive(isValid);
        technicalReview.setEndDate(expiryDate);

        technicalReview.setVehicle(vehicle);
        vehicle.setTechnicalReview(technicalReview);

        LOGGER.info("Technical review check for vehicle: {}", vehicle.getPlateNumber());
    }

    public TechnicalReviewResponse technicalReviewCheckExternal(String plateNumber) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("email", "vovakof205@ploncy.com");
        requestBody.add("plate", plateNumber);
        requestBody.add("gdrp", "1");
        requestBody.add("terms", "1");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<TechnicalReviewResponse> response;
        try {
            response = restTemplate.exchange(
                    TECHNICAL_REVIEW_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    TechnicalReviewResponse.class
            );
        } catch (Exception e) {
            LOGGER.error("Technical review: {}", e.getMessage());
            return null;
        }

        return response.getBody();
    }

    @Transactional
    public void technicalReviewScheduler() {
        List<Vehicle> expiringTechnicalReviewVehicles = vehicleRepository.findVehiclesWithExpiringTechnicalReview();
        expiringTechnicalReviewVehicles.forEach(vehicle -> {
            technicalReviewCheck(vehicle);
            vehicleRepository.save(vehicle);
        });
    }
}
