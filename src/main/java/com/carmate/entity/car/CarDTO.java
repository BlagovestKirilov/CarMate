package com.carmate.entity.car;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {
    private Long id;

    private String name;
    //account name vmesto status olichne lowo

    private String accountName;

    private String plateNumber;

    private String egn;

    private String deviceID;

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
