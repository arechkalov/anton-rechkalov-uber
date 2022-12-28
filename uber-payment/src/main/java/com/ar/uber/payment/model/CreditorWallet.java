package com.ar.uber.payment.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_creditor_wallet")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CreditorWallet {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private BigDecimal balance;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

}
