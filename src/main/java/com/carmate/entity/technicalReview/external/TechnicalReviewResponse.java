package com.carmate.entity.technicalReview.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicalReviewResponse {
    private int error;
    private String errortext;
    private String module;
    private String function;
    private String data;
}
