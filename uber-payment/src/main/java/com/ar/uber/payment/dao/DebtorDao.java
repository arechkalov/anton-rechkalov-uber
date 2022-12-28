package com.ar.uber.payment.dao;

import org.springframework.data.repository.CrudRepository;

import com.ar.uber.payment.model.DebtorWallet;

public interface DebtorDao extends CrudRepository<DebtorWallet, Long> {

    DebtorWallet findByAccountNumber(String accountNumber);
}
