package com.carmate.repository;

import com.carmate.entity.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT c FROM Vehicle c JOIN c.vignette v WHERE v.endDate <= CURRENT_DATE + 1 MONTH OR v.isActive = false")
    List<Vehicle> findVehiclesWithExpiringVignettes();

    @Query("SELECT c FROM Vehicle c JOIN c.insurance i WHERE i.endDate <= CURRENT_DATE + 1 MONTH OR i.isActive = false")
    List<Vehicle> findVehiclesWithExpiringInsurance();

    @Query("SELECT c FROM Vehicle c JOIN c.technicalReview t WHERE t.endDate <= CURRENT_DATE + 1 MONTH OR t.isActive = false")
    List<Vehicle> findVehiclesWithExpiringTechnicalReview();
}
