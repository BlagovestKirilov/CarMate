package com.carmate.entity.vignette;


import com.carmate.entity.car.Car;
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
    private Car car;

    private Boolean isActive = Boolean.FALSE;

    private Date startDate;

    private Date endDate;
}
