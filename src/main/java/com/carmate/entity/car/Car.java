package com.carmate.entity.car;

import com.carmate.entity.account.Account;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

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
