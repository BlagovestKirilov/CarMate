package com.carmate.controller;

import com.carmate.entity.car.Car;
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

    @GetMapping("/get-car/{deviceID}")
    public ResponseEntity<List<CarDTO>> getCar(@PathVariable String deviceID) {
        List<CarDTO> resultCars = carService.getCarsByDeviceID(deviceID);
        return ResponseEntity.ok(resultCars);
    }

    @DeleteMapping("/delete-car/{carID}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carID) {
        System.out.println("Delete car: " + carID);
         //carService.deleteCar(carID); // Uncomment when the actual delete operation is implemented
        return ResponseEntity.ok().build(); // Returns an HTTP 200 OK response with no body
    }
}
