package com.carmate.entity.insurance;

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
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isActive;
    private String insurer;
    private Date startDate;
    private Date endDate;

    @OneToOne(mappedBy = "insurance")
    private Vehicle vehicle;
}
