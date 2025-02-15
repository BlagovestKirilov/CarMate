package com.carmate.entity.obligation.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Obligation {
    private int obligationID;
    private int status;
    private int amount;
    private int discountAmount;
    private String bankName;
    private String bic;
    private String iban;
    private String paymentReason;
    private String pepCin;
    private String expirationDate;
    private int applicantID;
    private String obligedPersonName;
    private String obligedPersonIdent;
    private int obligedPersonIdentType;
    private String obligationDate;
    private String obligationIdentifier;
    private int type;
    private int serviceInstanceID;
    private int serviceID;
    private Map<String, String> additionalData;
    private int andSourceId;
    private List<PaymentRequest> paymentRequests;
}
