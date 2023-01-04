package com.ar.uber.payment.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class Customer { // remove seems it is never used.

    @Id
    private Long id;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

}
