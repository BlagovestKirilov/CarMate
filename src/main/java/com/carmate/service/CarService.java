package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.car.Car;
import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.entity.car.OilChangeDTO;
import com.carmate.entity.insurance.Insurance;
import com.carmate.entity.insurance.external.InsuranceResponse;
import com.carmate.entity.obligation.Obligation;
import com.carmate.entity.obligation.external.ObligationResponseResult;
import com.carmate.entity.technicalReview.TechnicalReview;
import com.carmate.entity.technicalReview.external.TechnicalReviewResponse;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.entity.vignette.Vignette;
import com.carmate.entity.vignette.external.VignetteResponse;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.repository.*;
import com.carmate.service.external.InsuranceService;
import com.carmate.service.external.ObligationService;
import com.carmate.service.external.TechnicalReviewService;
import com.carmate.service.external.VignetteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final VignetteService vignetteService;
    private final InsuranceService insuranceService;
    private final ObligationService obligationService;
    private final TechnicalReviewService technicalReviewService;
    private final AccountRepository accountRepository;
    private final TripSheetRepository tripSheetRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    @Autowired
    public CarService(
            CarRepository carRepository,
            VignetteService vignetteService,
            InsuranceService insuranceService,
            ObligationService obligationService,
            TechnicalReviewService technicalReviewService,
            AccountRepository accountRepository,
            TripSheetRepository tripSheetRepository
    ) {
        this.carRepository = carRepository;
        this.vignetteService = vignetteService;
        this.insuranceService = insuranceService;
        this.obligationService = obligationService;
        this.technicalReviewService = technicalReviewService;
        this.accountRepository = accountRepository;
        this.tripSheetRepository = tripSheetRepository;
    }

    public void saveCar(CarSaveDTO carSaveDTO) {
        if(carSaveDTO != null) {
            Car car = new Car();
            String username = getPrincipalUserName();
            car.setAccount(accountRepository.findByEmail(username).get());
            car.setName(carSaveDTO.getName());
            car.setPlateNumber(carSaveDTO.getPlateNumber());
            car.setEgn(carSaveDTO.getEgn());
            car.setDeviceID(carSaveDTO.getDeviceID());
            externalServicesCheck(car);
            carRepository.save(car);
            LOGGER.info("Saved car: {} to account: {}", carSaveDTO.getPlateNumber(), username);
        }
    }

    public List<CarDTO> getCars() {
        String username = getPrincipalUserName();
        System.out.println("Get car: " + username);

        Account account = accountRepository.findByEmail(username).get();
        List<Car> cars = account.getRole().equals(AccountRoleEnum.USER) ?
                account.getCars() : carRepository.findAll();
        return  cars
                .stream()
                .map(car -> CarDTO.builder()
                        .id(car.getId())
                        .name(car.getName())
                        .plateNumber(car.getPlateNumber())
                        .egn(car.getEgn())
                        .deviceID(car.getDeviceID())
                        .isActiveVignette(car.getVignette().getIsActive())
                        .startVignetteActiveDate(car.getVignette().getStartDate())
                        .endVignetteActiveDate(car.getVignette().getEndDate())
                        .isActiveInsurance(car.getInsurance().getIsActive())
                        .insurer(car.getInsurance().getInsurer())
                        .startInsuranceActiveDate(car.getInsurance().getStartDate())
                        .endInsuranceActiveDate(car.getInsurance().getEndDate())
                        .obligationsCount(car.getObligation().getObligationsCount())
                        .obligationSumAmount(car.getObligation().getObligationSumAmount())
                        .isActiveTechnicalReview(car.getTechnicalReview().getIsActive())
                        .endTechnicalReviewActiveDate(car.getTechnicalReview().getEndDate())
                        .oilChangeOdometer(car.getOilChangeOdometer())
                        .oilChangeDate(car.getOilChangeDate())
                        .build())
                .toList();
    }

    public void deleteCar(Long carID){
        carRepository.deleteById(carID);
        LOGGER.info("Delete car: {}", carID);
    }

    private void externalServicesCheck(Car car){
        vignetteCheck(car);
        insuranceCheck(car);
        obligationCheck(car);
        technicalReviewCheck(car);
    }
    public void vignetteCheck(Car car){
        VignetteResponse vignetteResponse = vignetteService.vignetteCheck(car.getPlateNumber());

        Vignette vignette = car.getVignette() != null ? car.getVignette() : new Vignette();

        if(vignetteResponse.getVignette() != null) {
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

        vignette.setCar(car);
        car.setVignette(vignette);
    }

    public void insuranceCheck(Car car){
        InsuranceResponse insuranceResponse = insuranceService.insuranceCheck(car.getPlateNumber());
        Insurance insurance = car.getInsurance() != null ? car.getInsurance() : new Insurance();
        if(insuranceResponse.getInsurer() != null) {
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

        insurance.setCar(car);
        car.setInsurance(insurance);
    }

    public void obligationCheck(Car car){
        ObligationResponseResult obligationResponseResult = obligationService.obligationCheck(car.getPlateNumber(), car.getEgn());

        Obligation obligation = car.getObligation() != null ? car.getObligation() : new Obligation();

        if(obligationResponseResult != null) {
            obligation.setObligationsCount(obligationResponseResult.getObligationsCount());
            obligation.setObligationSumAmount(obligationResponseResult.getObligationSumAmount());
        } else {
            obligation.setObligationsCount(0);
            obligation.setObligationSumAmount(0);
        }

        obligation.setCar(car);
        car.setObligation(obligation);
    }

    public void technicalReviewCheck(Car car){
        TechnicalReviewResponse technicalReviewResponse = technicalReviewService.technicalReviewCheck(car.getPlateNumber());
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

    public void saveTripSheet(TripSheetDTO tripSheetDTO) {
        if(tripSheetDTO != null) {

            Car car = carRepository.findById(tripSheetDTO.getCarID()).orElse(null);
            if(car != null) {
                TripSheet tripSheet = TripSheet.builder()
                        .car(car)
                        .departureDate(tripSheetDTO.getDepartureDate())
                        .departureTime(tripSheetDTO.getDepartureTime())
                        .departureLocation(tripSheetDTO.getDepartureLocation())
                        .tripReason(tripSheetDTO.getTripReason())
                        .arrivalDate(tripSheetDTO.getArrivalDate())
                        .arrivalTime(tripSheetDTO.getArrivalTime())
                        .arrivalLocation(tripSheetDTO.getArrivalLocation())
                        .startOdometer(tripSheetDTO.getStartOdometer())
                        .endOdometer(tripSheetDTO.getEndOdometer())
                        .build();
                tripSheetRepository.save(tripSheet);
                LOGGER.info("Trip Sheet saved to car {}{}", car.getPlateNumber(), car.getId());
            }
        }
    }
    public void saveOilChange(OilChangeDTO oilChangeDTO) {
        Car car = carRepository.findById(oilChangeDTO.getCarId()).orElse(null);
        if(car != null) {
            car.setOilChangeOdometer(oilChangeDTO.getOdometerValue());
            car.setOilChangeDate(new Date());
            carRepository.save(car);
        }
    }

    public List<TripSheetDTO> getTripSheets(Long carID) {
        Car car = carRepository.findById(carID).orElse(null);
        List<TripSheetDTO> tripSheetsDTOs = new ArrayList<>();
        if(car != null) {
            List<TripSheet> tripSheets = tripSheetRepository.findAllByCarOrderByArrivalDateDescArrivalTimeDesc(car);
            tripSheetsDTOs =  mapToTripSheetDTOs(tripSheets);
        }
        return tripSheetsDTOs;
    }

    public List<TripSheetDTO> getTripSheetsAdmin() {
        List<TripSheet> tripSheets = tripSheetRepository.findAll();
        return mapToTripSheetDTOs(tripSheets);
    }

    private List<TripSheetDTO> mapToTripSheetDTOs(List<TripSheet> tripSheets) {
        return tripSheets
                .stream()
                .map(tripSheet -> TripSheetDTO.builder()
                        .id(tripSheet.getId())
                        .carID(tripSheet.getCar().getId())
                        .carName(tripSheet.getCar().getName())
                        .departureDate(tripSheet.getDepartureDate())
                        .departureTime(tripSheet.getDepartureTime())
                        .departureLocation(tripSheet.getDepartureLocation())
                        .tripReason(tripSheet.getTripReason())
                        .arrivalDate(tripSheet.getArrivalDate())
                        .arrivalTime(tripSheet.getArrivalTime())
                        .arrivalLocation(tripSheet.getArrivalLocation())
                        .startOdometer(tripSheet.getStartOdometer())
                        .endOdometer(tripSheet.getEndOdometer())
                        .build())
                .toList();
    }

    public void deleteTripSheet(Long tripSheetID) {
        tripSheetRepository.deleteById(tripSheetID);
        LOGGER.info("Delete trip sheet : {}", tripSheetID);
    }

    private String getPrincipalUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
}
