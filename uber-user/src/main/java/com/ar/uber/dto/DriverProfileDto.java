package com.ar.uber.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.ar.uber.model.Vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfileDto {

    private DriverDto driver;

    private Set<Vehicle> vehicles;

    private Float ratingScore;

    private BigDecimal avgTripPrice;
}
