package com.carmate.service.external;

import com.carmate.entity.car.Car;
import com.carmate.entity.obligation.external.Obligation;
import com.carmate.entity.obligation.external.ObligationResponse;
import com.carmate.entity.obligation.external.ObligationResponseResult;
import com.carmate.entity.obligation.external.ObligationsData;
import com.carmate.repository.CarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ObligationService {
    @Autowired
    private CarRepository carRepository;

    private static final String OBLIGATION_ENDPOINT = "https://e-uslugi.mvr.bg/api/Obligations/AND?obligatedPersonType=1&additinalDataForObligatedPersonType=3&mode=1&obligedPersonIdent=%s&foreignVehicleNumber=%s";

    @Transactional
    public void obligationCheck(Car car) {
        ObligationResponseResult obligationResponseResult = obligationCheckExternal(car.getPlateNumber(), car.getEgn());

        com.carmate.entity.obligation.Obligation obligation = car.getObligation() != null ? car.getObligation() : new com.carmate.entity.obligation.Obligation();

        if (obligationResponseResult != null) {
            obligation.setObligationsCount(obligationResponseResult.getObligationsCount());
            obligation.setObligationSumAmount(obligationResponseResult.getObligationSumAmount());
        } else {
            obligation.setObligationsCount(0);
            obligation.setObligationSumAmount(0);
        }

        obligation.setCar(car);
        car.setObligation(obligation);
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
            System.out.println("Error"); //TODO
        }
        return obligationResponseResult;
    }

    @Transactional
    public void obligationScheduler() {
        List<Car> cars = carRepository.findAll();
        for (Car car : cars) {
            obligationCheck(car);
            carRepository.save(car);
        }
    }
}
