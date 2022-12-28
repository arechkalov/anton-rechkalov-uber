package com.ar.uber.payment.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.uber.payment.dao.CreditorDao;
import com.ar.uber.payment.dao.DebtorDao;
import com.ar.uber.payment.dao.PaymentDao;
import com.ar.uber.payment.model.CreditorWallet;
import com.ar.uber.payment.model.DebtorWallet;
import com.ar.uber.payment.model.Payment;
import com.ar.uber.payment.model.PaymentType;
import com.ar.uber.payment.service.CircuitBreakerService;
import com.ar.uber.payment.service.PaymentService;
import com.ar.uber.payment.web.exception.EntityNotFoundException;
import com.ar.uber.payment.web.exception.ValidationException;
import com.ar.uber.payment.web.request.PaymentAuthorizationRequest;
import com.ar.uber.payment.web.request.PaymentConfirmationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public static final String FAILED = "FAILED";
    public static final String AUTHORIZED = "AUTHORIZED";
    public static final String CONFIRMED = "CONFIRMED";

    private final CreditorDao creditorDao;
    private final DebtorDao debtorDao;
    private final PaymentDao paymentDao;
    private final CircuitBreakerService circuitBreakerService;

    @Override
    @Async
    @Transactional(rollbackFor=IllegalStateException.class)
    public void authorizePayment(final PaymentAuthorizationRequest authorizePaymentRequest, final String paymentCorrelationId) throws InterruptedException {

        final CreditorWallet creditorWallet = creditorDao.findByAccountNumber(authorizePaymentRequest.getCustomerAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Creditor does not exist with account number: " + authorizePaymentRequest.getCustomerAccountNumber()));
        final DebtorWallet debtorWallet = debtorDao.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Debtor does not exist"));
        BigDecimal totalAmount = authorizePaymentRequest.getAmount().add(authorizePaymentRequest.getReward());
        checkForAvailableCurrentBalance(authorizePaymentRequest.getCallBackUrl(), creditorWallet, totalAmount);
        final Payment payment = Payment.builder()
                .correlationId(paymentCorrelationId)
                .amount(authorizePaymentRequest.getAmount())
                .initiationTime(authorizePaymentRequest.getPaymentInitiationTime())
                .paymentType(PaymentType.valueOf(authorizePaymentRequest.getPaymentType()))
                .creditorWallet(creditorWallet)
                .debtorWallet(debtorWallet)
                .build();
        paymentDao.save(payment);
        circuitBreakerService.resilientSendAuthorizationCallbackRequest(authorizePaymentRequest.getCallBackUrl(), AUTHORIZED);
    }



    @Override
    @Async
    @Transactional
    public void confirmPayment(final String paymentCorrelationId, final PaymentConfirmationRequest paymentConfirmationRequest) throws InterruptedException {
        Thread.sleep(5000);
        Payment payment = paymentDao.findByCorrelationId(paymentCorrelationId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        checkForAvailableCurrentBalance(paymentConfirmationRequest.getCallBackUrl(), payment.getCreditorWallet(), paymentConfirmationRequest.getTotalAmount());
        BigDecimal deductedAmount = payment.getCreditorWallet().getBalance().subtract(paymentConfirmationRequest.getTotalAmount());
        payment.getCreditorWallet().setBalance(deductedAmount);
        payment.setConfirmTime(ZonedDateTime.now());
        circuitBreakerService.resilientSendConfirmationCallbackRequest(paymentConfirmationRequest.getCallBackUrl(),CONFIRMED);
    }

    private void checkForAvailableCurrentBalance(final String callBackUrl, final CreditorWallet wallet, final BigDecimal amount) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal subtract = balance.subtract(amount);
        if (subtract.compareTo(BigDecimal.ZERO) < 0) {
            circuitBreakerService.resilientSendConfirmationCallbackRequest(callBackUrl, FAILED);
            throw new ValidationException("Insufficient Funds");
        }
    }
}
