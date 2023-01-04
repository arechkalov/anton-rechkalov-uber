package com.ar.uber.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.uber.dao.PaymentRepository;
import com.ar.uber.payment.model.Payment;
import com.ar.uber.payment.model.PaymentStatus;
import com.ar.uber.service.PaymentService;
import com.ar.uber.web.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void updatePayment(final String correlationId, final PaymentStatus paymentStatus) { // make an object instead of these arguments
        log.info("updating status of current payment");
        Payment payment = paymentRepository.getByCorrelationId(correlationId)
                .orElseThrow(EntityNotFoundException::new);
        payment.setStatus(paymentStatus);
    }

    @Override
    @Transactional
    public Payment get(final String correlationId) { // return type should not be entity. create a model for payment.
        return paymentRepository.getByCorrelationId(correlationId)
                .orElseThrow(EntityNotFoundException::new);
    }

}
