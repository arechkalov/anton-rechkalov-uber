package com.ar.uber.payment.web.request;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;

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
public class PaymentAuthorizationRequest {
    private String paymentType;
    private BigDecimal amount;
    private BigDecimal reward;
    private String customerAccountNumber;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime paymentInitiationTime;
    private String callBackUrl;
}
