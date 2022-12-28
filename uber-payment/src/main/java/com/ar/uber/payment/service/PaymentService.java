package com.ar.uber.payment.service;

import com.ar.uber.payment.web.request.PaymentAuthorizationRequest;
import com.ar.uber.payment.web.request.PaymentConfirmationRequest;

public interface PaymentService {

    void authorizePayment(final PaymentAuthorizationRequest authorizePaymentRequest, String paymentCorrelationId) throws InterruptedException;

    void confirmPayment(String paymentCorrelationId, final PaymentConfirmationRequest paymentConfirmationRequest) throws InterruptedException;
}
