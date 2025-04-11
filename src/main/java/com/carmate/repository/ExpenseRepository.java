package com.carmate.repository;

import com.carmate.entity.vehicle.Vehicle;
import com.carmate.entity.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByVehicle(Vehicle vehicle);
}
