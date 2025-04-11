package com.carmate.entity.expense;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;

    private Long vehicleID;

    private String type;

    private BigDecimal amount;

    private String description;

    private Date date;
}
