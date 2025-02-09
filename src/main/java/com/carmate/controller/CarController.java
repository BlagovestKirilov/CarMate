package com.carmate.controller;

import com.carmate.entity.car.CarDTO;
import com.carmate.entity.car.CarSaveDTO;
import com.carmate.service.CarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
public class CarController {
    @Autowired
    CarServiceImpl carService;

    @PostMapping("/save-car")
    public ResponseEntity<CarSaveDTO> addCar(@RequestBody CarSaveDTO car) {
        System.out.println("Save car: " + car.getName()+ " " + car.getPlateNumber()+ " " +car.getEgn());
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
        System.out.println("Delete car: " + carID);
         carService.deleteCar(carID);
        return ResponseEntity.ok().build();
    }
}
