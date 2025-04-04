package com.carmate.entity.vehicleSupport;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSupportDTO {
    private Long id;

    private String name;

    private String type;

    private String town;

    private String address;

    private String phoneNumber;
}
