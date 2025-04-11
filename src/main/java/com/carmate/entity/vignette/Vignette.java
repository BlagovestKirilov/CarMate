package com.carmate.entity.vignette;


import com.carmate.entity.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vignette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "vignette")
    private Vehicle vehicle;

    private Boolean isActive = Boolean.FALSE;

    private Date startDate;

    private Date endDate;
}
