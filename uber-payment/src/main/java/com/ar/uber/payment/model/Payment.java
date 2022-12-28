package com.ar.uber.payment.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Payment {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal amount;

    @Column(name = "correlation_id", nullable = false, unique = true)
    private String correlationId;

    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @OneToOne
    @JoinColumn(name = "fk_creditor_id", referencedColumnName = "id")
    private CreditorWallet creditorWallet;

    @OneToOne
    @JoinColumn(name = "fk_debtor_id", referencedColumnName = "id")
    private DebtorWallet debtorWallet;


    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime initiationTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime confirmTime;

}
