package com.ar.uber.payment.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_debtor_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DebtorWallet {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal balance;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
}
