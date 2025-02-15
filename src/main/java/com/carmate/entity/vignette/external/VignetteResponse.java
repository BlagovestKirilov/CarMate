package com.carmate.entity.vignette.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VignetteResponse {
    private Vignette vignette;
    private boolean ok;
    private Status status;
}

