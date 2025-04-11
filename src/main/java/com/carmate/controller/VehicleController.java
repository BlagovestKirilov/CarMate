package com.carmate.controller;

import com.carmate.entity.vehicle.VehicleDTO;
import com.carmate.entity.vehicle.VehicleSaveDTO;
import com.carmate.entity.vehicle.OilChangeDTO;
import com.carmate.entity.expense.ExpenseDTO;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class VehicleController {

    private final VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/save-vehicle")
    public ResponseEntity<VehicleSaveDTO> saveVehicle(@RequestBody VehicleSaveDTO vehicle) {
        vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/get-vehicle")
    public ResponseEntity<List<VehicleDTO>> getVehicle() {
        List<VehicleDTO> resultVehicles = vehicleService.getVehicles();
        return ResponseEntity.ok(resultVehicles);
    }

    @DeleteMapping("/delete-vehicle/{vehicleID}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleID) {
        try {
            vehicleService.deleteVehicle(vehicleID);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/get-trip-sheet/{vehicleID}")
    public ResponseEntity<List<TripSheetDTO>> getTripSheet(@PathVariable Long vehicleID) {
        try {
            List<TripSheetDTO> tripSheetDTOS = vehicleService.getTripSheets(vehicleID);
            return ResponseEntity.ok(tripSheetDTOS);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/save-trip-sheet")
    public ResponseEntity<TripSheetDTO> saveTripSheet(@RequestBody TripSheetDTO tripSheetDTO) {
        try {
            vehicleService.saveTripSheet(tripSheetDTO);
            return ResponseEntity.ok(tripSheetDTO);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/oil-change")
    public ResponseEntity<OilChangeDTO> saveTripSheet(@RequestBody OilChangeDTO oilChangeDTO) {
        try {
            vehicleService.saveOilChange(oilChangeDTO);
            return ResponseEntity.ok(oilChangeDTO);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/save-expense")
    public ResponseEntity<ExpenseDTO> saveExpense(@RequestBody ExpenseDTO expenseDTO) {
        try {
            vehicleService.saveExpense(expenseDTO);
            return ResponseEntity.ok(expenseDTO);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/get-expense/{vehicleID}")
    public ResponseEntity<List<ExpenseDTO>> getExpense(@PathVariable Long vehicleID) {
        try {
            List<ExpenseDTO> expenseDTOS = vehicleService.getExpenses(vehicleID);
            return ResponseEntity.ok(expenseDTOS);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/delete-expense/{expenseID}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseID) {
        try {
            vehicleService.deleteExpense(expenseID);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
