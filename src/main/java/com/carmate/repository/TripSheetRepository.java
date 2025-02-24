package com.carmate.repository;

import com.carmate.entity.car.Car;
import com.carmate.entity.tripSheet.TripSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripSheetRepository extends JpaRepository<TripSheet, Long> {
    List<TripSheet> findAllByCarOrderByArrivalDateDescArrivalTimeDesc(Car car);
}
