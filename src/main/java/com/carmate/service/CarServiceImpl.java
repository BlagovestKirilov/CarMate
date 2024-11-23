package com.carmate.service;

import com.carmate.entity.car.Car;
import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.entity.insurance.InsuranceResponse;
import com.carmate.entity.obligation.ObligationResponseResult;
import com.carmate.entity.technicalReview.TechnicalReviewResponse;
import com.carmate.entity.vignette.VignetteResponse;
import com.carmate.repository.CarRepository;
import com.carmate.service.external.InsuranceServiceImpl;
import com.carmate.service.external.ObligationServiceImpl;
import com.carmate.service.external.TechnicalReviewServiceImpl;
import com.carmate.service.external.VignetteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarServiceImpl {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private VignetteServiceImpl vignetteService;

    @Autowired
    private InsuranceServiceImpl insuranceService;

    @Autowired
    private ObligationServiceImpl obligationService;

    @Autowired
    private TechnicalReviewServiceImpl technicalReviewService;

    public void saveCar(CarSaveDTO carSaveDTO) {
        if(carSaveDTO != null) {
            Car car = new Car();
            car.setName(carSaveDTO.getName());
            car.setPlateNumber(carSaveDTO.getPlateNumber());
            car.setEgn(carSaveDTO.getEgn());
            car.setDeviceID(carSaveDTO.getDeviceID());
            externalServicesCheck(car);
            carRepository.save(car);
        }
    }

    public List<CarDTO> getCarsByDeviceID(String deviceID) {
        System.out.println("Get car: " + deviceID);
        return carRepository.findAllByDeviceIDOrderById(deviceID)
                .stream()
                .map(car -> CarDTO.builder()
                        .id(car.getId())
                        .name(car.getName())
                        .plateNumber(car.getPlateNumber())
                        .egn(car.getEgn())
                        .deviceID(car.getDeviceID())
                        .isActiveVignette(car.getIsActiveVignette())
                        .startVignetteActiveDate(car.getStartVignetteActiveDate())
                        .endVignetteActiveDate(car.getEndVignetteActiveDate())
                        .isActiveInsurance(car.getIsActiveInsurance())
                        .insurer(car.getInsurer())
                        .startInsuranceActiveDate(car.getStartInsuranceActiveDate())
                        .endInsuranceActiveDate(car.getEndInsuranceActiveDate())
                        .obligationsCount(car.getObligationsCount())
                        .obligationSumAmount(car.getObligationSumAmount())
                        .isActiveTechnicalReview(car.getIsActiveTechnicalReview())
                        .endTechnicalReviewActiveDate(car.getEndTechnicalReviewActiveDate())
                        .build())
                .toList();
    }

    public void deleteCar(Long carId){
        carRepository.deleteById(carId);
    }

    private void externalServicesCheck(Car car){
        vignetteCheck(car);
        insuranceCheck(car);
        obligationCheck(car);
        technicalReviewCheck(car);
    }
    public void vignetteCheck(Car car){
        VignetteResponse vignetteResponse = vignetteService.vignetteCheck(car.getPlateNumber());
        if(vignetteResponse.getVignette() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                car.setStartVignetteActiveDate(dateFormat.parse(vignetteResponse.getVignette().getValidityDateFromFormated()));
                car.setEndVignetteActiveDate(dateFormat.parse(vignetteResponse.getVignette().getValidityDateToFormated()));
                car.setIsActiveVignette(Boolean.TRUE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            car.setIsActiveVignette(Boolean.FALSE);
        }
    }

    public void insuranceCheck(Car car){
        InsuranceResponse insuranceResponse = insuranceService.insuranceCheck(car.getPlateNumber());
        if(insuranceResponse.getInsurer() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                car.setStartInsuranceActiveDate(dateFormat.parse(insuranceResponse.getStartDate()));
                car.setEndInsuranceActiveDate(dateFormat.parse(insuranceResponse.getEndDate()));
                car.setInsurer(insuranceResponse.getInsurer());
                car.setIsActiveInsurance(Boolean.TRUE);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            car.setIsActiveInsurance(Boolean.FALSE);
        }
    }

    public void obligationCheck(Car car){
        ObligationResponseResult obligationResponseResult = obligationService.obligationCheck(car.getPlateNumber(), car.getEgn());
        car.setObligationsCount(obligationResponseResult.getObligationsCount());
        car.setObligationSumAmount(obligationResponseResult.getObligationSumAmount());
    }

    public void technicalReviewCheck(Car car){
        TechnicalReviewResponse technicalReviewResponse = technicalReviewService.technicalReviewCheck(car.getPlateNumber());
        boolean isValid = false;
        Date expiryDate = null;

        // Pattern to check for "Има валиден периодичен технически преглед!"
        Pattern validPattern = Pattern.compile("Има валиден периодичен технически преглед!");
        Matcher validMatcher = validPattern.matcher(technicalReviewResponse.getData());

        if (validMatcher.find()) {
            isValid = true;
            // Pattern to extract the date after "валиден до"
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
        car.setIsActiveTechnicalReview(isValid);
        car.setEndTechnicalReviewActiveDate(expiryDate);
    }
}
