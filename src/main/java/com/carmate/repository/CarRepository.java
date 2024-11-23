package com.carmate.repository;

import com.carmate.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findAllByDeviceIDOrderById(String deviceID);

    List<Car> findAllByEndVignetteActiveDateIsBeforeOrIsActiveVignetteIsFalse(Date date);

    List<Car> findAllByEndInsuranceActiveDateIsBeforeOrIsActiveInsuranceIsFalse(Date date);

    List<Car> findAllByEndTechnicalReviewActiveDateIsBeforeOrIsActiveTechnicalReviewIsFalse(Date date);
}
