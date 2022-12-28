package com.ar.uber.payment.web;

import java.util.concurrent.ForkJoinPool;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.ar.uber.payment.model.PaymentType;
import com.ar.uber.payment.service.CircuitBreakerService;
import com.ar.uber.payment.service.PaymentService;
import com.ar.uber.payment.web.exception.ValidationException;
import com.ar.uber.payment.web.request.PaymentAuthorizationRequest;
import com.ar.uber.payment.web.request.PaymentConfirmationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payments/{paymentCorrelationId}")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private static final String FAILED = "FAILED";

    private final PaymentService paymentService;
    private final CircuitBreakerService circuitBreakerService;

    @PutMapping("authorization")
    @ResponseBody
    public DeferredResult<ResponseEntity<?>> authorize(@RequestBody PaymentAuthorizationRequest authorizePaymentRequest, @PathVariable String paymentCorrelationId) {
        checkPaymentType(authorizePaymentRequest.getCallBackUrl(), authorizePaymentRequest.getPaymentType());
        log.info("Received payment-authorization request");
        final DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        ForkJoinPool.commonPool().submit(() -> {
            try {
                Thread.sleep(1000);
                paymentService.authorizePayment(authorizePaymentRequest, paymentCorrelationId);
                output.setResult(ResponseEntity.ok().build());
            } catch (Exception e ) {
                log.error("exception occurred", e);
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(e.getMessage()));
            }
        });
        final ResponseEntity<Object> accepted = ResponseEntity.accepted().build();
        output.setResult(accepted);
        return output;
    }

    @PutMapping("confirmation")
    public DeferredResult<ResponseEntity<?>> confirm(@PathVariable String paymentCorrelationId,
            @RequestBody PaymentConfirmationRequest paymentConfirmationRequest) {
        log.info("Received payment-confirmation request");
        final DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            try {
                Thread.sleep(5000);
                paymentService.confirmPayment(paymentCorrelationId, paymentConfirmationRequest);
                output.setResult(ResponseEntity.ok().build());
            } catch (Exception e) {
                log.error("exception occurred", e);
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(e.getMessage()));
            }
        });
        final ResponseEntity<Object> accepted = ResponseEntity.accepted().build();
        output.setResult(accepted);
        return output;
    }

    private void checkPaymentType(final String callBackUrl, final String paymentType) {
        try {
            PaymentType.valueOf(paymentType);
        } catch (IllegalArgumentException e) {
            log.error("{} payment method is not supported by current payment system", paymentType, e);
            circuitBreakerService.resilientSendAuthorizationCallbackRequest(callBackUrl, FAILED);
            throw new ValidationException("Payment method is not supported");
        }
    }

}
