package com.carmate.entity.tripSheet;


import com.carmate.entity.car.Car;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private LocalTime departureTime;

    @Column(nullable = false)
    private String departureLocation;

    @Column(nullable = false)
    private String tripReason;

    @Column(nullable = false)
    private LocalDate arrivalDate;

    @Column(nullable = false)
    private LocalTime arrivalTime;

    @Column(nullable = false)
    private String arrivalLocation;

    @Column(nullable = false)
    private Long startOdometer;

    @Column(nullable = false)
    private Long endOdometer;
}
