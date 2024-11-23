package com.carmate.service.external;

import com.carmate.entity.technicalReview.TechnicalReviewResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class TechnicalReviewServiceImpl {

    private static final String TECHNICAL_REVIEW_ENDPOINT = "https://myve.bg/api/get/gtp";
    public TechnicalReviewResponse technicalReviewCheck(String plateNumber) {
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
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody,headers);
        ResponseEntity<TechnicalReviewResponse> response;
        try{
            response = restTemplate.exchange(
                    TECHNICAL_REVIEW_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    TechnicalReviewResponse.class
            );
        }catch (Exception e){
            System.out.println("Error"); //TODO
            return null;
        }


        return response.getBody();
    }
}
