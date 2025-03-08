package com.carmate.controller;

import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.entity.car.OilChangeDTO;
import com.carmate.entity.expense.ExpenseDTO;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/save-car")
    public ResponseEntity<CarSaveDTO> addCar(@RequestBody CarSaveDTO car) {
        carService.saveCar(car);
        return ResponseEntity.ok(car);
    }

    @GetMapping("/get-car")
    public ResponseEntity<List<CarDTO>> getCar() {
        List<CarDTO> resultCars = carService.getCars();
        return ResponseEntity.ok(resultCars);
    }

    @DeleteMapping("/delete-car/{carID}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carID) {
        carService.deleteCar(carID);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save-trip-sheet")
    public ResponseEntity<TripSheetDTO> saveTripSheet(@RequestBody TripSheetDTO tripSheetDTO) {
        carService.saveTripSheet(tripSheetDTO);
        return ResponseEntity.ok(tripSheetDTO);
    }

    @PostMapping("/oil-change")
    public ResponseEntity<OilChangeDTO> saveTripSheet(@RequestBody OilChangeDTO oilChangeDTO) {
        carService.saveOilChange(oilChangeDTO);
        return ResponseEntity.ok(oilChangeDTO);
    }

    @PostMapping("/save-expense")
    public ResponseEntity<ExpenseDTO> saveExpense(@RequestBody ExpenseDTO expenseDTO) {
        carService.saveExpense(expenseDTO);
        return ResponseEntity.ok(expenseDTO);
    }

    @GetMapping("/get-expense/{carID}")
    public ResponseEntity<List<ExpenseDTO>> getExpense(@PathVariable Long carID) {
        List<ExpenseDTO> expenseDTOS = carService.getExpenses(carID);
        return ResponseEntity.ok(expenseDTOS);
    }

    @DeleteMapping("/delete-expense/{expenseID}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseID) {
        carService.deleteExpense(expenseID);
        return ResponseEntity.ok().build();
    }
}
