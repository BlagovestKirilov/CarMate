package com.carmate.entity.vignette;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vignette {
    private String licensePlateNumber;
    private String country;
    private boolean exempt;
    private String vignetteNumber;
    private String vehicleClass;
    private String emissionsClass;
    private String validityDateFromFormated;
    private String validityDateFrom;
    private String validityDateToFormated;
    private String validityDateTo;
    private String issueDateFormated;
    private String issueDate;
    private int price;
    private String currency;
    private String status;
    private boolean whitelist;
    private String vehicleType;
    private String vehicleClassCode;
    private String emissionsClassCode;
    private String vehicleTypeCode;
    private boolean statusBoolean;
}