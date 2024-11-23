package com.carmate.entity.obligation;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class PaymentRequest {
    private int paymentRequestID;
    private int registrationDataID;
    private int registrationDataType;
    private int status;
    private int obligationID;
    private String obligedPersonName;
    private String obligedPersonIdent;
    private int obligedPersonIdentType;
    private String sendDate;
    private String payDate;
    private String externalPortalPaymentNumber;
    private int amount;
    private Map<String, String> additionalData;
    private String payerIdent;
    private int payerIdentType;
}
