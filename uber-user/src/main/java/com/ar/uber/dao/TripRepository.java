package com.ar.uber.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.ar.uber.model.Trip;

public interface TripRepository extends CrudRepository<Trip, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select t from Trip t where t.id = ?1")
    Optional<Trip> findOneOptimistic(Long tripId);

    List<Trip> findAllByDriverId(Long id);

    Optional<Trip> findByPaymentCorrelationId(String correlationId);

    List<Trip> findByDriverAppUserPhoneNumber(String phoneNumber);
}
