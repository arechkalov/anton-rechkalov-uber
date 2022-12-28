package com.ar.uber.service;

import com.ar.uber.payment.model.Payment;
import com.ar.uber.payment.model.PaymentStatus;

public interface PaymentService {

    void updatePayment(String correlationId, PaymentStatus paymentStatus);

    Payment get(String correlationId);
}
