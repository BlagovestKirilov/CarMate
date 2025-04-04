package com.carmate.entity.vehicleSupport;

import com.carmate.enums.VehicleSupportType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private VehicleSupportType type;

    private String town;

    private String address;

    private String phoneNumber;
}
