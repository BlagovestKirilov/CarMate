package com.carmate.entity.vehicle;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OilChangeDTO {
    private Long vehicleID;
    private Long odometerValue;
}
