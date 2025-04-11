package com.carmate.service.external;

import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.obligation.external.Obligation;
import com.carmate.entity.obligation.external.ObligationResponse;
import com.carmate.entity.obligation.external.ObligationResponseResult;
import com.carmate.entity.obligation.external.ObligationsData;
import com.carmate.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ObligationService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public ObligationService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    private static final String OBLIGATION_ENDPOINT = "https://e-uslugi.mvr.bg/api/Obligations/AND?obligatedPersonType=1&additinalDataForObligatedPersonType=3&mode=1&obligedPersonIdent=%s&foreignVehicleNumber=%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(ObligationService.class);

    @Transactional
    public void obligationCheck(Vehicle vehicle) {
        ObligationResponseResult obligationResponseResult = obligationCheckExternal(vehicle.getPlateNumber(), vehicle.getEgn());

        com.carmate.entity.obligation.Obligation obligation = vehicle.getObligation() != null ? vehicle.getObligation() : new com.carmate.entity.obligation.Obligation();

        if (obligationResponseResult != null) {
            obligation.setObligationsCount(obligationResponseResult.getObligationsCount());
            obligation.setObligationSumAmount(obligationResponseResult.getObligationSumAmount());
        } else {
            obligation.setObligationsCount(0);
            obligation.setObligationSumAmount(0);
        }

        obligation.setVehicle(vehicle);
        vehicle.setObligation(obligation);

        LOGGER.info("Obligation check for car: {}", vehicle.getPlateNumber());
    }

    public ObligationResponseResult obligationCheckExternal(String plateNumber, String egn) {
        ObligationResponseResult obligationResponseResult = new ObligationResponseResult();
        obligationResponseResult.setObligationsCount(0);
        obligationResponseResult.setObligationSumAmount(0);
        RestTemplate restTemplate = new RestTemplate();

        String url = String.format(OBLIGATION_ENDPOINT, egn, plateNumber);
        try {
            ResponseEntity<ObligationResponse> response = restTemplate.getForEntity(url, ObligationResponse.class);
            ObligationResponse obligationResponse = response.getBody();
            assert obligationResponse != null;
            for (ObligationsData obligationsData : obligationResponse.getObligationsData()) {
                obligationResponseResult.setObligationsCount(obligationResponseResult.getObligationsCount() + obligationsData.getObligations().size());
                for (Obligation obligation : obligationsData.getObligations()) {
                    obligationResponseResult.setObligationSumAmount(obligationResponseResult.getObligationSumAmount() + obligation.getAmount());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Obligation check failed", e);
        }
        return obligationResponseResult;
    }

    @Transactional
    public void obligationScheduler() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle vehicle : vehicles) {
            obligationCheck(vehicle);
            vehicleRepository.save(vehicle);
        }
    }
}
