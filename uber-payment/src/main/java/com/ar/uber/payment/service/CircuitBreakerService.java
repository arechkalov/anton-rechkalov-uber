package com.ar.uber.payment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerService {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Retryable(maxAttempts = 2, recover = "authorizationFallback")
    public String resilientSendAuthorizationCallbackRequest(final String callBackUrl, final String status) {
        return retryTemplate.execute(context -> {
            log.info(String.format("Retry count %d", context.getRetryCount()));
            return sendAuthorizationCallbackRequest(callBackUrl, status);
        });
    }

    @Retryable(maxAttempts = 2, recover = "confirmationFallback")
    public String resilientSendConfirmationCallbackRequest(final String callBackUrl, final String status) {
        return retryTemplate.execute(context -> {
            log.info(String.format("Retry count %d", context.getRetryCount()));
            return sendConfirmationCallbackRequest(callBackUrl, status);
        });
    }

    @Recover
    public String confirmationFallback(ResourceAccessException e, final String callbackUrl, String status) {
        log.warn("Call for {} failed", callbackUrl);
        throw new IllegalStateException("Could not reach server transaction will roll back");
    }

    private String sendAuthorizationCallbackRequest(final String callBackUrl, String status) {
        log.info("sending callback request");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(callBackUrl, status, String.class);
        log.info("{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity.getStatusCode().toString();
    }

    private String sendConfirmationCallbackRequest(final String callBackUrl, String status) {
        log.info("sending callback request");
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(callBackUrl, status, String.class);
        log.info("{} {}", responseEntity.getStatusCode(), responseEntity.getBody());
        return responseEntity.getStatusCode().toString();
    }
}
