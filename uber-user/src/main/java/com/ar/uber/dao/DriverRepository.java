package com.ar.uber.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ar.uber.model.Driver;

public interface DriverRepository extends CrudRepository<Driver, Long> {

    Optional<Driver> findByAppUserPhoneNumber(String phoneNumber);

}
