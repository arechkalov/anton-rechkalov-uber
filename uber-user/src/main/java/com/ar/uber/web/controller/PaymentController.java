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
// Add swagger and java documentations for all public methods
//Use @ControllerAdvice and create custom ApiError.class and Http.Status response for negative scenarios such as EntityNotFound, UserNotFound etc. exceptions.
public class PaymentController {

    public static final String STATUS_AUTHORIZED = "AUTHORIZED"; // use string representation in enum instead of string
    public static final String STATUS_CONFIRMED = "CONFIRMED"; // use string representation in enum instead of string

    private final PaymentService paymentService;
    private final TripService tripService;

    @PostMapping("{correlationId}/authorization") // use put mapping instead
    //use annotation @ResponseStatus with NO_CONTENT instead of returning ResponseEntity and make method void.
    public ResponseEntity<?> authorize(@PathVariable String correlationId, @RequestBody String status) { // add PaymentAuthorizeRequest and wrap in it status as paymentStatus object with string property Status
        log.info("received payment authorization callback for {}", correlationId);
        if (STATUS_AUTHORIZED.equals(status)) {
            paymentService.updatePayment(correlationId, PENDING_CONFIRMATION); // move all ifs logic to paymentService and treat exceptions with @ControllerAdvice.
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        else {
            paymentService.updatePayment(correlationId, FAILED);
            TripInfoDto tripInfoDto = tripService.abortTrip(correlationId);
            return ResponseEntity.badRequest().body(tripInfoDto); // add an error description and make it in a separate class in case of failed trip validation failed and add more details to the message, using @ControllerAdvice methods.
        }
    }

    @PostMapping("{correlationId}/confirmation") //use put mapping instead
    //use annotation @ResponseStatus with NO_CONTENT instead of returning ResponseEntity and make method void.
    public ResponseEntity<?> confirm(@PathVariable String correlationId, @RequestBody String status) { // add PaymentConfirmRequest and wrap in it PaymentStatus value object with String property status
        log.info("received payment confirmation callback for {}", correlationId);
        if (STATUS_CONFIRMED.equals(status)) {
            paymentService.updatePayment(correlationId, PaymentStatus.SUCCEEDED); // move all ifs logic to paymentService and treat exceptions with @ControllerAdvice.
        } else {
            paymentService.updatePayment(correlationId, FAILED); // add an error description and make it in a separate class in case of failed trip validation failed and add more details to the message, using @ControllerAdvice methods.
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
