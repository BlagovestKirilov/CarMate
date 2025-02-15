package com.carmate.service.external;

import com.carmate.entity.vignette.external.VignetteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VignetteService {
    @Autowired
    private RestTemplate restTemplate;
    private static final String BG_TOLL_ENDPOINT = "https://check.bgtoll.bg/check/vignette/plate/BG/";

    private static final Logger logger = LoggerFactory.getLogger(VignetteService.class);

    public VignetteResponse vignetteCheck(String plateNumber) {
        try {
            ResponseEntity<VignetteResponse> response = restTemplate.getForEntity(BG_TOLL_ENDPOINT + plateNumber, VignetteResponse.class);
            if (response.getBody() != null) {
                logger.info("Successful checking vignette for : " + plateNumber);
                return response.getBody();
            }
        } catch (Exception e) {
            logger.error("Unsuccessful checking vignette for : " + plateNumber, e.getMessage(), e);
        }

        return null;
    }
}
