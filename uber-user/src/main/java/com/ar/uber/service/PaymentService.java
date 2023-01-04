package com.ar.uber.service;

import com.ar.uber.payment.model.Payment;
import com.ar.uber.payment.model.PaymentStatus;

// add javadocs for all methods
public interface PaymentService {

    void updatePayment(String correlationId, PaymentStatus paymentStatus); // use one object instead of 2 args

    Payment get(String correlationId);
}
