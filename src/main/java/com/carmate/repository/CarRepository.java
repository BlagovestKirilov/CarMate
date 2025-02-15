package com.carmate.repository;

import com.carmate.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findAllByVignette_EndDateIsBeforeOrVignette_IsActiveIsFalse(Date endDate);

    List<Car> findAllByInsurance_EndDateIsBeforeOrVignette_IsActiveIsFalse(Date endDate);

    List<Car> findAllByTechnicalReview_EndDateIsBeforeOrVignette_IsActiveIsFalse(Date endDate);
}
