package com.carmate.entity.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSaveDTO {

    private String name;

    private String plateNumber;

    private String egn;
}
