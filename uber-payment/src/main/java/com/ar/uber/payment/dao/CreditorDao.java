package com.ar.uber.payment.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ar.uber.payment.model.CreditorWallet;

public interface CreditorDao extends CrudRepository<CreditorWallet, Long> {

    Optional<CreditorWallet> findByAccountNumber(String accountNumber);
}
