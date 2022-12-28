package com.ar.uber.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ar.uber.payment.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> getByCorrelationId(String correlationId);
}
