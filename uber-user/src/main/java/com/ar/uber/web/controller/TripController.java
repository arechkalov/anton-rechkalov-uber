package com.ar.uber.web.controller;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.ar.uber.appuser.AppUser;
import com.ar.uber.dto.TripDto;
import com.ar.uber.payment.model.Payment;
import com.ar.uber.service.CircuitBreakerService;
import com.ar.uber.service.PaymentService;
import com.ar.uber.service.TripService;
import com.ar.uber.web.request.PaymentAuthorizationRequest;
import com.ar.uber.web.request.TripCompletionRequest;
import com.ar.uber.web.request.TripCreateRequest;
import com.ar.uber.web.response.TripCreatedResponse;
import com.ar.uber.web.response.TripInfoDto;
import com.ar.uber.web.response.TripsResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Slf4j
public class TripController {

    private final TripService tripService;
    private final CircuitBreakerService circuitBreakerService;
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<TripsResponse> getAll() {
        final AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final List<TripDto> tripsResponse = tripService.getAll(appUser.getId());
        final TripsResponse response = new TripsResponse(tripsResponse);
        return new ResponseEntity<>(response, OK);
    }

    @PostMapping
    public ResponseEntity<TripCreatedResponse> addTrip(@RequestBody TripCreateRequest tripCreateRequest) {
        final TripCreatedResponse tripCreatedResponse = tripService.add(tripCreateRequest);
        return new ResponseEntity<>(tripCreatedResponse, CREATED);
    }

    @PutMapping(value = "{tripId}/acquisition")
    public ResponseEntity<TripInfoDto> acquireTrip(@PathVariable Long tripId) {
        final AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final TripInfoDto tripinfoDto = tripService.acquireTrip(appUser, tripId);
        final Payment payment = paymentService.get(tripinfoDto.getPaymentCorrelationId()); //TODO add DTO
        final HttpEntity<PaymentAuthorizationRequest> requestHttpEntity = new HttpEntity<>(PaymentAuthorizationRequest.builder()
                .paymentType(payment.getPaymentType())
                .reward(payment.getReward())
                .amount(payment.getPrice())
                .customerAccountNumber(payment.getCustomerAccountNumber())
                .paymentInitiationTime(ZonedDateTime.now())
                .callBackUrl("http://localhost:8080/api/v1/payments/" + tripinfoDto.getPaymentCorrelationId() + "/authorization")
                .build());
        circuitBreakerService.resilientSendAuthorizationCallbackRequest(tripinfoDto, requestHttpEntity);
        return new ResponseEntity<>(tripinfoDto, OK);
    }

    @PutMapping(value = "{tripId}/confirmation")
    public ResponseEntity<TripInfoDto> confirmTrip(@PathVariable Long tripId, @RequestBody TripCompletionRequest tripCompletionRequest) {
        final AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final TripInfoDto trip = tripService.confirmTrip(appUser, tripId, tripCompletionRequest.getFinalAmount(), tripCompletionRequest.getDriverRating());
        circuitBreakerService.resilientSendConfirmationCallbackRequest(trip.getPaymentCorrelationId(), tripCompletionRequest);
        return new ResponseEntity<>(trip, OK);
    }

}
