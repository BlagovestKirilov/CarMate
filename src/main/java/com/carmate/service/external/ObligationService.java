package com.carmate.service.external;

import com.carmate.entity.obligation.Obligation;
import com.carmate.entity.obligation.ObligationResponse;
import com.carmate.entity.obligation.ObligationResponseResult;
import com.carmate.entity.obligation.ObligationsData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ObligationService {

    private static final String OBLIGATION_ENDPOINT = "https://e-uslugi.mvr.bg/api/Obligations/AND?obligatedPersonType=1&additinalDataForObligatedPersonType=3&mode=1&obligedPersonIdent=%s&foreignVehicleNumber=%s";

    public ObligationResponseResult obligationCheck(String plateNumber, String egn) {
        ObligationResponseResult obligationResponseResult = new ObligationResponseResult();
        obligationResponseResult.setObligationsCount(0);
        obligationResponseResult.setObligationSumAmount(0);
        RestTemplate restTemplate = new RestTemplate();

        String url = String.format(OBLIGATION_ENDPOINT, egn, plateNumber);
        try {
            ResponseEntity<ObligationResponse> response = restTemplate.getForEntity(url, ObligationResponse.class);
            ObligationResponse obligationResponse = response.getBody();
            assert obligationResponse != null;
            for(ObligationsData obligationsData : obligationResponse.getObligationsData()){
                obligationResponseResult.setObligationsCount(obligationResponseResult.getObligationsCount() + obligationsData.getObligations().size());
                for (Obligation obligation : obligationsData.getObligations()){
                    obligationResponseResult.setObligationSumAmount(obligationResponseResult.getObligationSumAmount() + obligation.getAmount());
                }
            }
        }catch (Exception e){
            System.out.println("Error"); //TODO
        }
        return obligationResponseResult;
    }
}
