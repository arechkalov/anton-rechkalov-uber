package com.ar.uber.web.request;

import java.math.BigDecimal;

import com.ar.uber.model.DriverRating;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TripCompletionRequest {
    private BigDecimal finalAmount;
    private DriverRating driverRating;
}
