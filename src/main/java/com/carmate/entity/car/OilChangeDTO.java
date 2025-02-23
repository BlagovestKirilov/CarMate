package com.carmate.entity.car;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OilChangeDTO {
    private Long carId;
    private Long odometerValue;
}
