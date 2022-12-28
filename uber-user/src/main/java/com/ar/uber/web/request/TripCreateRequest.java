package com.ar.uber.web.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TripCreateRequest {

    private String customerAccountNumber;
    private BigDecimal reward;
    private Location startLocation;
    private Location endLocation;
    private BigDecimal initialPrice;
    private String paymentType;
}
