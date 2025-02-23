package com.carmate.entity.tripSheet;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripSheetDTO {
    private Long carID;

    private LocalDate departureDate;

    private LocalTime departureTime;

    private String departureLocation;

    private String tripReason;

    private LocalDate arrivalDate;

    private LocalTime arrivalTime;

    private String arrivalLocation;

    private Long startOdometer;

    private Long endOdometer;
}
