package com.carmate.repository;

import com.carmate.entity.vehicleSupport.VehicleSupport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleSupportRepository extends JpaRepository<VehicleSupport, Long> {
}
