package com.carmate.repository;

import com.carmate.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c JOIN c.vignette v WHERE v.endDate <= CURRENT_DATE + 1 MONTH OR v.isActive = false")
    List<Car> findCarsWithExpiringVignettes();

    @Query("SELECT c FROM Car c JOIN c.insurance i WHERE i.endDate <= CURRENT_DATE + 1 MONTH OR i.isActive = false")
    List<Car> findCarsWithExpiringInsurance();

    @Query("SELECT c FROM Car c JOIN c.technicalReview t WHERE t.endDate <= CURRENT_DATE + 1 MONTH OR t.isActive = false")
    List<Car> findCarsWithExpiringTechnicalReview();
}
