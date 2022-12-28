package com.ar.uber.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.PUT;

import com.ar.uber.web.request.PaymentAuthorizationRequest;
import com.ar.uber.web.request.PaymentConfirmationRequest;
import com.ar.uber.web.request.TripCompletionRequest;
import com.ar.uber.web.response.TripInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerService {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Retryable(maxAttempts = 2, recover = "authorizationFallback")
    public String resilientSendAuthorizationCallbackRequest(final TripInfoDto tripinfoDto,
            final HttpEntity<PaymentAuthorizationRequest> requestHttpEntity) {
        return retryTemplate.execute(context -> {
            log.info(String.format("Retry count %d", context.getRetryCount()));
            return callbackForPaymentAuthorization(tripinfoDto, requestHttpEntity);
        });
    }

    @Retryable(maxAttempts = 2, recover = "confirmationFallback")
    public String resilientSendConfirmationCallbackRequest(final String paymentCorrelationId, final TripCompletionRequest tripCompletionRequest) {
        return retryTemplate.execute(context -> {
            log.info(String.format("Retry count %d", context.getRetryCount()));
            return callbackForPaymentConfirmation(paymentCorrelationId, tripCompletionRequest);
        });
    }

    @Recover
    public String authorizationFallback(ResourceAccessException e, final TripInfoDto tripinfoDto, final HttpEntity<PaymentAuthorizationRequest> requestHttpEntity) {
        log.warn("Call for {} failed", requestHttpEntity.getBody().getCallBackUrl());
        throw new IllegalStateException("Could not reach server transaction will roll back");
    }

    @Recover
    public String confirmationFallback(ResourceAccessException e, final String paymentCorrelationId, final TripCompletionRequest tripCompletionRequest) {
        log.warn("Call for {} failed", "http://localhost:8080/api/v1/payments/" + paymentCorrelationId + "/confirmation");
        throw new IllegalStateException("Could not reach server transaction will roll back");
    }

    private String callbackForPaymentAuthorization(final TripInfoDto tripinfoDto, final HttpEntity<PaymentAuthorizationRequest> requestHttpEntity) {
        final String uri = "http://localhost:8081/api/v1/payments/"+ tripinfoDto.getPaymentCorrelationId() +"/authorization"; //TODO externalize
        log.info("sending payment with correlation id {} for authorization", tripinfoDto.getPaymentCorrelationId());
        final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, PUT, requestHttpEntity, String.class);//TODO add Hystrix for circuit breaking
        log.info("{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity.getStatusCode().toString();
    }

    private String callbackForPaymentConfirmation(final String paymentCorrelationId, final TripCompletionRequest tripCompletionRequest) {
        final HttpEntity<PaymentConfirmationRequest> requestHttpEntity = new HttpEntity<>(PaymentConfirmationRequest.builder()
                .totalAmount(tripCompletionRequest.getFinalAmount())
                .callBackUrl("http://localhost:8080/api/v1/payments/" + paymentCorrelationId + "/confirmation")
                .build());

        final String uri = "http://localhost:8081/api/v1/payments/"+ paymentCorrelationId +"/confirmation"; //TODO externalize
        log.info("sending payment with correlation id {} for confirmation", paymentCorrelationId);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, PUT, requestHttpEntity, String.class);//TODO add Hystrix for circuit breaking
        log.info("{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity.getStatusCode().toString();
    }
}
