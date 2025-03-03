package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.car.Car;
import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.entity.car.OilChangeDTO;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.repository.CarRepository;
import com.carmate.repository.TripSheetRepository;
import com.carmate.security.util.AuthService;
import com.carmate.service.external.InsuranceService;
import com.carmate.service.external.ObligationService;
import com.carmate.service.external.TechnicalReviewService;
import com.carmate.service.external.VignetteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final VignetteService vignetteService;
    private final InsuranceService insuranceService;
    private final ObligationService obligationService;
    private final TechnicalReviewService technicalReviewService;
    private final TripSheetRepository tripSheetRepository;
    private final AuthService authService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    @Autowired
    public CarService(
            CarRepository carRepository,
            VignetteService vignetteService,
            InsuranceService insuranceService,
            ObligationService obligationService,
            TechnicalReviewService technicalReviewService,
            TripSheetRepository tripSheetRepository,
            AuthService authService
    ) {
        this.carRepository = carRepository;
        this.vignetteService = vignetteService;
        this.insuranceService = insuranceService;
        this.obligationService = obligationService;
        this.technicalReviewService = technicalReviewService;
        this.tripSheetRepository = tripSheetRepository;
        this.authService = authService;
    }

    public void saveCar(CarSaveDTO carSaveDTO) {
        if (carSaveDTO != null) {
            Car car = new Car();
            Account account = authService.getAccountByPrincipal();
            car.setAccount(account);
            car.setName(carSaveDTO.getName());
            car.setPlateNumber(carSaveDTO.getPlateNumber());
            car.setEgn(carSaveDTO.getEgn());
            externalServicesCheck(car);
            carRepository.save(car);
            LOGGER.info("Saved car: {} to account: {}", carSaveDTO.getPlateNumber(), account.getEmail());
        }
    }

    public List<CarDTO> getCars() {
        Account account = authService.getAccountByPrincipal();
        LOGGER.info("Get car: {}", account.getEmail());

        List<Car> cars = account.getRole().equals(AccountRoleEnum.USER) ?
                account.getCars() : carRepository.findAll();
        return cars
                .stream()
                .map(car -> CarDTO.builder()
                        .id(car.getId())
                        .name(car.getName())
                        .accountName(car.getAccount().getAccountName())
                        .plateNumber(car.getPlateNumber())
                        .egn(car.getEgn())
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

    public void deleteCar(Long carID) {
        carRepository.deleteById(carID);
        LOGGER.info("Delete car: {}", carID);
    }

    public void externalServicesCheck(Car car) {
        vignetteService.vignetteCheck(car);
        insuranceService.insuranceCheck(car);
        obligationService.obligationCheck(car);
        technicalReviewService.technicalReviewCheck(car);
    }

    public void saveTripSheet(TripSheetDTO tripSheetDTO) {
        if (tripSheetDTO != null) {

            Car car = carRepository.findById(tripSheetDTO.getCarID()).orElse(null);
            if (car != null) {
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
        if (car != null) {
            car.setOilChangeOdometer(oilChangeDTO.getOdometerValue());
            car.setOilChangeDate(new Date());
            carRepository.save(car);
        }
    }

    public List<TripSheetDTO> getTripSheets(Long carID) {
        Car car = carRepository.findById(carID).orElse(null);
        List<TripSheetDTO> tripSheetsDTOs = new ArrayList<>();
        if (car != null) {
            List<TripSheet> tripSheets = tripSheetRepository.findAllByCarOrderByArrivalDateDescArrivalTimeDesc(car);
            tripSheetsDTOs = mapToTripSheetDTOs(tripSheets);
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
                        .accountName(tripSheet.getCar().getAccount().getAccountName())
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
}
