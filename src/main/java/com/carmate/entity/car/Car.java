package com.carmate.entity.car;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

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
}
