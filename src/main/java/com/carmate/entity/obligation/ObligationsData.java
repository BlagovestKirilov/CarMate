package com.carmate.entity.obligation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObligationsData {
    private int unitGroup;
    private boolean errorNoDataFound;
    private boolean errorReadingData;
    private List<Obligation> obligations;
}
