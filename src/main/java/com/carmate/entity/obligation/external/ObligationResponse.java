package com.carmate.entity.obligation.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObligationResponse {
    private List<ObligationsData> obligationsData;
    private boolean hasNonHandedSlip;
    private boolean errorOnHasNonHandedSlip;
}
