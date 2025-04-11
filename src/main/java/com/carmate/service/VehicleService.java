package com.carmate.service;

import com.carmate.entity.account.Account;
import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.vehicle.VehicleDTO;
import com.carmate.entity.vehicle.VehicleSaveDTO;
import com.carmate.entity.vehicle.OilChangeDTO;
import com.carmate.entity.expense.Expense;
import com.carmate.entity.expense.ExpenseDTO;
import com.carmate.entity.tripSheet.TripSheet;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.enums.AccountRoleEnum;
import com.carmate.enums.ExpenseType;
import com.carmate.repository.VehicleRepository;
import com.carmate.repository.ExpenseRepository;
import com.carmate.repository.TripSheetRepository;
import com.carmate.service.external.InsuranceService;
import com.carmate.service.external.ObligationService;
import com.carmate.service.external.TechnicalReviewService;
import com.carmate.service.external.VignetteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VignetteService vignetteService;
    private final InsuranceService insuranceService;
    private final ObligationService obligationService;
    private final TechnicalReviewService technicalReviewService;
    private final TripSheetRepository tripSheetRepository;
    private final AuthService authService;
    private final ExpenseRepository expenseRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    public VehicleService(
            VehicleRepository vehicleRepository,
            VignetteService vignetteService,
            InsuranceService insuranceService,
            ObligationService obligationService,
            TechnicalReviewService technicalReviewService,
            TripSheetRepository tripSheetRepository,
            AuthService authService,
            ExpenseRepository expenseRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vignetteService = vignetteService;
        this.insuranceService = insuranceService;
        this.obligationService = obligationService;
        this.technicalReviewService = technicalReviewService;
        this.tripSheetRepository = tripSheetRepository;
        this.authService = authService;
        this.expenseRepository = expenseRepository;
    }

    public void saveVehicle(VehicleSaveDTO vehicleSaveDTO) {
        if (vehicleSaveDTO != null) {
            Vehicle vehicle = new Vehicle();
            Account account = authService.getAccountByPrincipal();
            vehicle.setAccount(account);
            vehicle.setName(vehicleSaveDTO.getName());
            vehicle.setPlateNumber(vehicleSaveDTO.getPlateNumber());
            vehicle.setEgn(vehicleSaveDTO.getEgn());
            externalServicesCheck(vehicle);
            vehicleRepository.save(vehicle);
            LOGGER.info("Saved vehicle: {} to account: {}", vehicleSaveDTO.getPlateNumber(), account.getEmail());
        }
    }

    public List<VehicleDTO> getVehicles() {
        Account account = authService.getAccountByPrincipal();
        LOGGER.info("Get vehicle: {}", account.getEmail());

        List<Vehicle> vehicles = account.getRole().equals(AccountRoleEnum.USER) ?
                account.getVehicles() : vehicleRepository.findAll();
        return vehicles
                .stream()
                .map(vehicle -> VehicleDTO.builder()
                        .id(vehicle.getId())
                        .name(vehicle.getName())
                        .accountName(vehicle.getAccount().getAccountName())
                        .plateNumber(vehicle.getPlateNumber())
                        .egn(vehicle.getEgn())
                        .isActiveVignette(vehicle.getVignette().getIsActive())
                        .startVignetteActiveDate(vehicle.getVignette().getStartDate())
                        .endVignetteActiveDate(vehicle.getVignette().getEndDate())
                        .isActiveInsurance(vehicle.getInsurance().getIsActive())
                        .insurer(vehicle.getInsurance().getInsurer())
                        .startInsuranceActiveDate(vehicle.getInsurance().getStartDate())
                        .endInsuranceActiveDate(vehicle.getInsurance().getEndDate())
                        .obligationsCount(vehicle.getObligation().getObligationsCount())
                        .obligationSumAmount(vehicle.getObligation().getObligationSumAmount())
                        .isActiveTechnicalReview(vehicle.getTechnicalReview().getIsActive())
                        .endTechnicalReviewActiveDate(vehicle.getTechnicalReview().getEndDate())
                        .oilChangeOdometer(vehicle.getOilChangeOdometer())
                        .oilChangeDate(vehicle.getOilChangeDate())
                        .build())
                .toList();
    }

    public void deleteVehicle(Long vehicleID) {
        Account account = authService.getAccountByPrincipal();
        Vehicle vehicle = vehicleRepository.findById(vehicleID)
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + vehicleID));

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        vehicleRepository.delete(vehicle);
        LOGGER.info("Delete vehicle: {}", vehicleID);
    }

    public void externalServicesCheck(Vehicle vehicle) {
        vignetteService.vignetteCheck(vehicle);
        insuranceService.insuranceCheck(vehicle);
        obligationService.obligationCheck(vehicle);
        technicalReviewService.technicalReviewCheck(vehicle);
    }

    public void saveTripSheet(TripSheetDTO tripSheetDTO) {
        if (tripSheetDTO != null) {
            Account account = authService.getAccountByPrincipal();
            Vehicle vehicle = vehicleRepository.findById(tripSheetDTO.getVehicleID())
                    .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + tripSheetDTO.getVehicleID()));

            if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
                throw new AccessDeniedException("You don't have permission to delete this vehicle");
            }

            TripSheet tripSheet = TripSheet.builder()
                    .vehicle(vehicle)
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
            LOGGER.info("Trip Sheet saved to vehicle {}{}", vehicle.getPlateNumber(), vehicle.getId());
        }
    }

    public void saveOilChange(OilChangeDTO oilChangeDTO) {
        Vehicle vehicle = vehicleRepository.findById(oilChangeDTO.getVehicleID())
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + oilChangeDTO.getVehicleID()));

        Account account = authService.getAccountByPrincipal();

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        vehicle.setOilChangeOdometer(oilChangeDTO.getOdometerValue());
        vehicle.setOilChangeDate(new Date());
        vehicleRepository.save(vehicle);
        LOGGER.info("Save oil change to vehicle with ID: {}", oilChangeDTO.getVehicleID());

    }

    public List<TripSheetDTO> getTripSheets(Long vehicleID) {
        Vehicle vehicle = vehicleRepository.findById(vehicleID)
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + vehicleID));

        Account account = authService.getAccountByPrincipal();

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        List<TripSheetDTO> tripSheetsDTOs = new ArrayList<>();
        if (vehicle != null) {
            List<TripSheet> tripSheets = tripSheetRepository.findAllByVehicleOrderByArrivalDateDescArrivalTimeDesc(vehicle);
            tripSheetsDTOs = mapToTripSheetDTOs(tripSheets);
        }
        LOGGER.info("Get trip sheets for vehicle with ID: {}", vehicleID);
        return tripSheetsDTOs;
    }

    public List<TripSheetDTO> getTripSheets() {
        List<TripSheet> tripSheets = tripSheetRepository.findAll();
        LOGGER.info("Admin get all trip sheets");
        return mapToTripSheetDTOs(tripSheets);
    }

    private List<TripSheetDTO> mapToTripSheetDTOs(List<TripSheet> tripSheets) {
        return tripSheets
                .stream()
                .map(tripSheet -> TripSheetDTO.builder()
                        .id(tripSheet.getId())
                        .vehicleID(tripSheet.getVehicle().getId())
                        .carName(tripSheet.getVehicle().getName())
                        .accountName(tripSheet.getVehicle().getAccount().getAccountName())
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

    public void saveExpense(ExpenseDTO expenseDTO) {
        Vehicle vehicle = vehicleRepository.findById(expenseDTO.getVehicleID())
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + expenseDTO.getVehicleID()));

        Account account = authService.getAccountByPrincipal();

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        Expense expense = Expense.builder()
                .vehicle(vehicle)
                .type(ExpenseType.valueOf(expenseDTO.getType()))
                .description(expenseDTO.getDescription())
                .amount(expenseDTO.getAmount())
                .date(new Date())
                .build();
        expenseRepository.save(expense);
        LOGGER.info("Saved expense for vehicle with id : {}", expenseDTO.getVehicleID());
    }

    public List<ExpenseDTO> getExpenses(Long vehicleID) {
        Vehicle vehicle = vehicleRepository.findById(vehicleID)
                .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + vehicleID));

        Account account = authService.getAccountByPrincipal();

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        List<ExpenseDTO> expenses = expenseRepository.findAllByVehicle(vehicle)
                .stream()
                .map(expense -> ExpenseDTO.builder()
                        .id(expense.getId())
                        .vehicleID(vehicle.getId())
                        .amount(expense.getAmount())
                        .type(expense.getType().toString())
                        .description(expense.getDescription())
                        .date(expense.getDate())
                        .build())
                .toList();

        LOGGER.info("Get expense for vehicle with id : {}", vehicleID);
        return expenses;
    }

    public void deleteExpense(Long expenseID) {
        Expense expense = expenseRepository.findById(expenseID)
                .orElseThrow(() -> new NoSuchElementException("Expense not found with id: " + expenseID));

        Vehicle vehicle = expense.getVehicle();
        Account account = authService.getAccountByPrincipal();

        if (!account.getRole().equals(AccountRoleEnum.ADMIN) && !vehicle.getAccount().equals(account)) {
            throw new AccessDeniedException("You don't have permission to delete this vehicle");
        }

        expenseRepository.delete(expense);
        LOGGER.info("Deleted expense with id : {}", expenseID);
    }
}
