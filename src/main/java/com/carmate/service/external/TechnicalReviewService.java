package com.carmate.service.external;

import com.carmate.entity.car.Car;
import com.carmate.entity.technicalReview.TechnicalReview;
import com.carmate.entity.technicalReview.external.TechnicalReviewResponse;
import com.carmate.repository.CarRepository;
import jakarta.transaction.Transactional;
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

    @Autowired
    private CarRepository carRepository;

    private static final String TECHNICAL_REVIEW_ENDPOINT = "https://myve.bg/api/get/gtp";

    @Transactional
    public void technicalReviewCheck(Car car) {
        TechnicalReviewResponse technicalReviewResponse = technicalReviewCheckExternal(car.getPlateNumber());
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
                    e.printStackTrace();
                }
            }
        }

        TechnicalReview technicalReview = car.getTechnicalReview() != null ? car.getTechnicalReview() : new TechnicalReview();
        technicalReview.setIsActive(isValid);
        technicalReview.setEndDate(expiryDate);

        technicalReview.setCar(car);
        car.setTechnicalReview(technicalReview);
    }

    public TechnicalReviewResponse technicalReviewCheckExternal(String plateNumber) {
        RestTemplate restTemplate = new RestTemplate();

        // Build the request body as a Map
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("email", "vovakof205@ploncy.com");
        requestBody.add("plate", plateNumber);
        requestBody.add("gdrp", "1");
        requestBody.add("terms", "1");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        // Create the request entity
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
            System.out.println("Error"); //TODO
            return null;
        }


        return response.getBody();
    }

    @Transactional
    public void technicalReviewScheduler() {
        Date currentDate = new Date();
        List<Car> carsForTechnicalReviewCheck = carRepository.findAllByTechnicalReview_EndDateIsBeforeOrVignette_IsActiveIsFalse(currentDate);
        for (Car car : carsForTechnicalReviewCheck) {
            technicalReviewCheck(car);
            carRepository.save(car);
        }
    }
}
