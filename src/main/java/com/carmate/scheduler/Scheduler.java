package com.carmate.scheduler;

import com.carmate.entity.car.Car;
import com.carmate.repository.CarRepository;
import com.carmate.service.CarServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class Scheduler  {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarServiceImpl carService;

   // @Scheduled(cron = "0 * * * * *")
    public void schedulerChecks(){
        vignetteScheduler();
        insuranceScheduler();
        technicalReviewScheduler();
        obligationScheduler();
        System.out.println("end");
    }

    @Transactional
    public void vignetteScheduler(){
        Date currentDate = new Date();
        List<Car> carsForVignetteCheck = carRepository.findAllByEndVignetteActiveDateIsBeforeOrIsActiveVignetteIsFalse(currentDate);
        for(Car car : carsForVignetteCheck){
            carService.vignetteCheck(car);
            carRepository.save(car);
        }
    }

    @Transactional
    public void insuranceScheduler(){
        Date currentDate = new Date();
        List<Car> carsForInsuranceCheck = carRepository.findAllByEndInsuranceActiveDateIsBeforeOrIsActiveInsuranceIsFalse(currentDate);
        for(Car car : carsForInsuranceCheck){
            carService.insuranceCheck(car);
            carRepository.save(car);
        }
    }

    @Transactional
    public void technicalReviewScheduler(){
        Date currentDate = new Date();
        List<Car> carsForTechnicalReviewCheck = carRepository.findAllByEndTechnicalReviewActiveDateIsBeforeOrIsActiveTechnicalReviewIsFalse(currentDate);
        for(Car car : carsForTechnicalReviewCheck){
            carService.technicalReviewCheck(car);
            carRepository.save(car);
        }
    }

    @Transactional
    public void obligationScheduler(){
        List<Car> cars = carRepository.findAll();
        for(Car car : cars){
            carService.obligationCheck(car);
            carRepository.save(car);
        }
    }
}
