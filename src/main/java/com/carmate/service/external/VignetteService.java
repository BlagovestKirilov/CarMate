package com.carmate.service.external;

import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.vignette.Vignette;
import com.carmate.entity.vignette.external.VignetteResponse;
import com.carmate.repository.VehicleRepository;
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
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VignetteService(RestTemplate restTemplate, VehicleRepository vehicleRepository) {
        this.restTemplate = restTemplate;
        this.vehicleRepository = vehicleRepository;
    }

    private static final String BG_TOLL_ENDPOINT = "https://check.bgtoll.bg/check/vignette/plate/BG/";

    private static final Logger LOGGER = LoggerFactory.getLogger(VignetteService.class);

    @Transactional
    public void vignetteCheck(Vehicle vehicle) {
        Vignette vignette = vehicle.getVignette() != null ? vehicle.getVignette() : new Vignette();

        VignetteResponse vignetteResponse = vignetteCheckExternal(vehicle.getPlateNumber());

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

        vehicle.setVignette(vignette);

        LOGGER.info("Vignette check for vehicle: {}", vehicle.getPlateNumber());
    }

    public VignetteResponse vignetteCheckExternal(String plateNumber) {
        try {
            ResponseEntity<VignetteResponse> response = restTemplate.getForEntity(BG_TOLL_ENDPOINT + plateNumber, VignetteResponse.class);
            if (response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            LOGGER.error("Unsuccessful checking vignette for : {}", plateNumber, e);
        }

        return null;
    }

    @Transactional
    public void vignetteScheduler() {
        List<Vehicle> expiringVignettesVehicles = vehicleRepository.findVehiclesWithExpiringVignettes();
        expiringVignettesVehicles.forEach(vehicle -> {
            vignetteCheck(vehicle);
            vehicleRepository.save(vehicle);
        });
    }
}
