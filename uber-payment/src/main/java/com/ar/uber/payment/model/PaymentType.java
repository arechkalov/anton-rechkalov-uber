package com.ar.uber.payment.model;

import lombok.Getter;

public enum PaymentType {
    AMEX("AMEX"),
    DISCOVER("DISCOVER"),
    JCB("JCB"),
    MASTERCARD("MASTERCARD"),
    VISA("VISA");

    @Getter
    final String value;

    PaymentType(final String value) {
        this.value = value;
    }

}
