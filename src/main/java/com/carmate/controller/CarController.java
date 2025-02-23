package com.carmate.controller;

import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.entity.car.OilChangeDTO;
import com.carmate.entity.tripSheet.TripSheetDTO;
import com.carmate.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class CarController {
    @Autowired
    CarService carService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CarController.class);

    @PostMapping("/save-car")
    public ResponseEntity<CarSaveDTO> addCar(@RequestBody CarSaveDTO car) {
        LOGGER.info("Save car: {} {} {}", car.getName(), car.getPlateNumber(), car.getEgn());
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
        LOGGER.info("Delete car: {}", carID);
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
}
