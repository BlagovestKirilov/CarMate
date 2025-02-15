package com.carmate.entity.insurance.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsuranceResponse {
    private String licensePlateNumber;
    private String insurer;
    private String startDate;
    private String endDate;
}
