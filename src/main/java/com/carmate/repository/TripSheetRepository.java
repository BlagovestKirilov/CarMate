package com.carmate.repository;

import com.carmate.entity.tripSheet.TripSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripSheetRepository extends JpaRepository<TripSheet, Long> {
}
