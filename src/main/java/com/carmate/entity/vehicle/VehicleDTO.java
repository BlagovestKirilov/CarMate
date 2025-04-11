package com.carmate.entity.vehicle;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;

    private String name;

    private String accountName;

    private String plateNumber;

    private String egn;

    private Boolean isActiveVignette;

    private Date startVignetteActiveDate;

    private Date endVignetteActiveDate;

    private Boolean isActiveInsurance;

    private String insurer;

    private Date startInsuranceActiveDate;

    private Date endInsuranceActiveDate;

    private Integer obligationsCount;

    private Integer obligationSumAmount;

    private Boolean isActiveTechnicalReview;

    private Date endTechnicalReviewActiveDate;

    private Long oilChangeOdometer;

    private Date oilChangeDate;
}
