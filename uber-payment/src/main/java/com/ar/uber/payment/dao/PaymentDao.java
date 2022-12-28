package com.ar.uber.payment.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ar.uber.payment.model.Payment;

public interface PaymentDao extends CrudRepository<Payment, Long> {

    Optional<Payment> findByCorrelationId(String correlationId);
}
