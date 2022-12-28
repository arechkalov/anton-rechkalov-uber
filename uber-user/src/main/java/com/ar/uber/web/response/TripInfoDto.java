package com.ar.uber.web.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TripInfoDto {
    private Long tripId;
    private String driverPhoneNumber;
    private BigDecimal price;
    private BigDecimal reward;
    private String paymentCorrelationId;
    private String paymentStatus;
    private String tripStatus;
}
