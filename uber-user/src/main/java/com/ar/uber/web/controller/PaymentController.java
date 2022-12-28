package com.ar.uber.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ar.uber.payment.model.PaymentStatus;
import com.ar.uber.service.PaymentService;
import com.ar.uber.service.TripService;
import com.ar.uber.web.response.TripInfoDto;

import static com.ar.uber.payment.model.PaymentStatus.FAILED;
import static com.ar.uber.payment.model.PaymentStatus.PENDING_CONFIRMATION;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    public static final String STATUS_AUTHORIZED = "AUTHORIZED";
    public static final String STATUS_CONFIRMED = "CONFIRMED";

    private final PaymentService paymentService;
    private final TripService tripService;

    @PostMapping("{correlationId}/authorization")
    public ResponseEntity<?> authorize(@PathVariable String correlationId, @RequestBody String status) {
        log.info("received payment authorization callback for {}", correlationId);
        if (STATUS_AUTHORIZED.equals(status)) {
            paymentService.updatePayment(correlationId, PENDING_CONFIRMATION);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        else {
            paymentService.updatePayment(correlationId, FAILED);
            TripInfoDto tripInfoDto = tripService.abortTrip(correlationId);
            return ResponseEntity.badRequest().body(tripInfoDto);
        }
    }

    @PostMapping("{correlationId}/confirmation")
    public ResponseEntity<?> confirm(@PathVariable String correlationId, @RequestBody String status) {
        log.info("received payment confirmation callback for {}", correlationId);
        if (STATUS_CONFIRMED.equals(status)) {
            paymentService.updatePayment(correlationId, PaymentStatus.SUCCEEDED);
        } else {
            paymentService.updatePayment(correlationId, FAILED);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
