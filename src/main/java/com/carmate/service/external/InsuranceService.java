package com.carmate.service.external;

import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.insurance.Insurance;
import com.carmate.entity.insurance.external.InsuranceResponse;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class InsuranceService {

    private final RestTemplate restTemplate;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public InsuranceService(RestTemplate restTemplate, VehicleRepository vehicleRepository) {
        this.restTemplate = restTemplate;
        this.vehicleRepository = vehicleRepository;
    }

    private static final String GUARANTEE_FUND_ENDPOINT = "https://www.guaranteefund.org/bg/%D0%B8%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%86%D0%B8%D0%BE%D0%BD%D0%B5%D0%BD-%D1%86%D0%B5%D0%BD%D1%82%D1%8A%D1%80-%D0%B8-%D1%81%D0%BF%D1%80%D0%B0%D0%B2%D0%BA%D0%B8/%D1%83%D1%81%D0%BB%D1%83%D0%B3%D0%B8/%D0%BF%D1%80%D0%BE%D0%B2%D0%B5%D1%80%D0%BA%D0%B0-%D0%B7%D0%B0-%D0%B2%D0%B0%D0%BB%D0%B8%D0%B4%D0%BD%D0%B0-%D0%B7%D0%B0%D1%81%D1%82%D1%80%D0%B0%D1%85%D0%BE%D0%B2%D0%BA%D0%B0-%D0%B3%D1%80a%D0%B6%D0%B4a%D0%BD%D1%81%D0%BAa-%D0%BE%D1%82%D0%B3%D0%BE%D0%B2%D0%BE%D1%80%D0%BD%D0%BE%D1%81%D1%82-%D0%BD%D0%B0-%D0%B0%D0%B2%D1%82%D0%BE%D0%BC%D0%BE%D0%B1%D0%B8%D0%BB%D0%B8%D1%81%D1%82%D0%B8%D1%82%D0%B5";

    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceService.class);

    @Transactional
    public void insuranceCheck(Vehicle vehicle) {
        InsuranceResponse insuranceResponse = insuranceCheckExternal(vehicle.getPlateNumber());
        Insurance insurance = vehicle.getInsurance() != null ? vehicle.getInsurance() : new Insurance();
        if (insuranceResponse.getInsurer() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                insurance.setStartDate(dateFormat.parse(insuranceResponse.getStartDate().replace("г.", "").replace("ч.", "").replaceAll("\\s+", " ").trim()));
                insurance.setEndDate(dateFormat.parse(insuranceResponse.getEndDate().replace("г.", "").replace("ч.", "").replaceAll("\\s+", " ").trim()));
                insurance.setInsurer(insuranceResponse.getInsurer());
                insurance.setIsActive(Boolean.TRUE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            insurance.setIsActive(Boolean.FALSE);
        }

        insurance.setVehicle(vehicle);
        vehicle.setInsurance(insurance);

        LOGGER.info("Insurance check for vehicle: {}", vehicle.getPlateNumber());
    }

    public InsuranceResponse insuranceCheckExternal(String plateNumber) {
        InsuranceResponse insuranceResponse = new InsuranceResponse();
        insuranceResponse.setLicensePlateNumber(plateNumber);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);

        // Prepare the request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("dkn", plateNumber);
        requestBody.add("rama", "");
        requestBody.add("stiker", "");
        requestBody.add("seria", "");
        requestBody.add("date", formattedDate);
        requestBody.add("send", "търси");

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    GUARANTEE_FUND_ENDPOINT,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (Exception e) {
            LOGGER.error("Unsuccessful insurance checking for : {}", plateNumber, e);
            return insuranceResponse;
        }
        String patternString = "<td style=\"background:#f2f2f2; text-align:left; padding:.3rem; font-weight:400;vertical-align:top\"><a[^>]*>([^<]*)</a></td>\\s*" +
                "<td[^>]*>([^<]*)</td>\\s*" +
                "<td[^>]*>([^<]*)</td>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(Objects.requireNonNull(response.getBody()));

        if (matcher.find()) {
            String insurer = matcher.group(1).trim();
            String startDate = matcher.group(2).trim();
            String endDate = matcher.group(3).trim();

            insuranceResponse.setInsurer(insurer);
            insuranceResponse.setStartDate(startDate.replace("/", "."));
            insuranceResponse.setEndDate(endDate.replace("/", "."));
        }

        return insuranceResponse;
    }

    @Transactional
    public void insuranceScheduler() {
        List<Vehicle> expiringInsuranceVehicles = vehicleRepository.findVehiclesWithExpiringInsurance();
        expiringInsuranceVehicles.forEach(vehicle -> {
            insuranceCheck(vehicle);
            vehicleRepository.save(vehicle);
        });
    }
}
