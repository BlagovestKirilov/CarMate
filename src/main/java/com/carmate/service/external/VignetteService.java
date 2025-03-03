package com.carmate.service.external;

import com.carmate.entity.car.Car;
import com.carmate.entity.vignette.Vignette;
import com.carmate.entity.vignette.external.VignetteResponse;
import com.carmate.repository.CarRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class VignetteService {

    private final RestTemplate restTemplate;
    private final CarRepository carRepository;

    @Autowired
    public VignetteService(RestTemplate restTemplate, CarRepository carRepository) {
        this.restTemplate = restTemplate;
        this.carRepository = carRepository;
    }

    private static final String BG_TOLL_ENDPOINT = "https://check.bgtoll.bg/check/vignette/plate/BG/";

    private static final Logger logger = LoggerFactory.getLogger(VignetteService.class);

    @Transactional
    public void vignetteCheck(Car car) {
        Vignette vignette = car.getVignette() != null ? car.getVignette() : new Vignette();

        VignetteResponse vignetteResponse = vignetteCheckExternal(car.getPlateNumber());

        if (vignetteResponse.getVignette() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                vignette.setStartDate(dateFormat.parse(vignetteResponse.getVignette().getValidityDateFromFormated()));
                vignette.setEndDate(dateFormat.parse(vignetteResponse.getVignette().getValidityDateToFormated()));
                vignette.setIsActive(Boolean.TRUE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            vignette.setIsActive(Boolean.FALSE);
        }

        car.setVignette(vignette);
    }

    public VignetteResponse vignetteCheckExternal(String plateNumber) {
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

    @Transactional
    public void vignetteScheduler() {
        List<Car> expiringVignettesCars = carRepository.findCarsWithExpiringVignettes();
        expiringVignettesCars.forEach(car -> {
            vignetteCheck(car);
            carRepository.save(car);
        });
    }
}
