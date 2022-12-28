package com.ar.uber.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import org.hibernate.StaleObjectStateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.uber.appuser.AppUser;
import com.ar.uber.dao.TripRepository;
import com.ar.uber.dto.TripDto;
import com.ar.uber.model.DriverRating;
import com.ar.uber.model.Location;
import com.ar.uber.model.Trip;
import com.ar.uber.model.TripStatus;
import com.ar.uber.payment.model.Payment;
import com.ar.uber.payment.model.PaymentStatus;
import com.ar.uber.service.TripService;
import com.ar.uber.service.converter.TripConverter;
import com.ar.uber.web.exception.EntityNotFoundException;
import com.ar.uber.web.exception.TripAlreadyAbortedException;
import com.ar.uber.web.exception.TripAlreadyAcquiredException;
import com.ar.uber.web.exception.TripAlreadyFinishedException;
import com.ar.uber.web.exception.TripNotFoundForCurrentUserException;
import com.ar.uber.web.request.TripCreateRequest;
import com.ar.uber.web.response.TripCreatedResponse;
import com.ar.uber.web.response.TripInfoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    private final TripConverter tripConverter;

    @Override
    @Transactional
    public TripCreatedResponse add(final TripCreateRequest tripCreateRequest) {
        final Payment payment = Payment.builder()
                .correlationId(UUID.randomUUID().toString())
                .price(tripCreateRequest.getInitialPrice())
                .reward(tripCreateRequest.getReward())
                .status(PaymentStatus.PENDING_AUTHORIZATION)
                .paymentType(tripCreateRequest.getPaymentType())
                .customerAccountNumber(tripCreateRequest.getCustomerAccountNumber())
                .build();

        final Trip trip = tripRepository.save(Trip.builder()
                .startLocation(Location.toEntity(tripCreateRequest.getStartLocation()))
                .endLocation(Location.toEntity(tripCreateRequest.getEndLocation()))
                .status(TripStatus.PENDING)
                .payment(payment)
                .build());
        payment.setTrip(trip);
        return new TripCreatedResponse(trip.getId());
    }

    @Override
    @Transactional
    public List<TripDto> getAll(final Long id) {
        final List<Trip> trips = tripRepository.findAllByDriverId(id);
        return trips.stream()
                .map(tripConverter::convert)
                .collect(toList());
    }

    @Override
    @Transactional
    public TripInfoDto acquireTrip(final AppUser appUser, final Long tripId) {
        final Trip trip;
        try {
            trip = tripRepository.findOneOptimistic(tripId)
                    .orElseThrow(EntityNotFoundException::new);
        } catch (StaleObjectStateException e) {
            log.error("Optimistic lock on acquiring trip occurred!", e);
            throw new TripAlreadyAcquiredException();
        }

        if (TripStatus.ACQUIRED.equals(trip.getStatus())) {
            throw new TripAlreadyAcquiredException();
        }

        trip.setDriver(appUser.getDriver());
        trip.setStatus(TripStatus.ACQUIRED);
        trip.setStartTime(ZonedDateTime.now());

        return TripInfoDto.builder()
                .driverPhoneNumber(appUser.getPhoneNumber())
                .tripId(tripId)
                .reward(trip.getPayment().getReward())
                .paymentStatus(trip.getPayment().getStatus().toString())
                .price(trip.getPayment().getPrice())
                .tripStatus(trip.getStatus().name())
                .paymentCorrelationId(trip.getPayment().getCorrelationId())
                .build();
    }

    @Override
    @Transactional
    public TripInfoDto abortTrip(final String paymentCorrelationId) {
        final Trip trip = tripRepository.findByPaymentCorrelationId(paymentCorrelationId)
                .orElseThrow(EntityNotFoundException::new);

        if (TripStatus.ABORTED.equals(trip.getStatus())) {
            throw new TripAlreadyAbortedException();
        }
        trip.setStatus(TripStatus.ABORTED);
        trip.setEndTime(ZonedDateTime.now());

        return TripInfoDto.builder()
                .driverPhoneNumber(trip.getDriver().getAppUser().getPhoneNumber())
                .tripId(trip.getId())
                .price(trip.getPayment().getPrice())
                .paymentCorrelationId(trip.getPayment().getCorrelationId())
                .reward(trip.getPayment().getReward())
                .tripStatus(trip.getStatus().name())
                .paymentStatus(trip.getPayment().getStatus().toString())
                .build();
    }

    @Override
    @Transactional
    public TripInfoDto confirmTrip(final AppUser appUser, final Long tripId,
            final BigDecimal finalAmount,
            final DriverRating driverRating) {
        final Trip trip = tripRepository.findById(tripId)
                .orElseThrow(EntityNotFoundException::new);

        if (TripStatus.COMPLETED.equals(trip.getStatus())) {
            throw new TripAlreadyFinishedException();
        }
        //check if this trip belongs to current driver
        if (isNull(trip.getDriver()) || !trip.getDriver().getAppUser().getPhoneNumber().equals(appUser.getPhoneNumber())) {
            throw new TripNotFoundForCurrentUserException();
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(ZonedDateTime.now());
        trip.setDriverRating(driverRating);
        trip.setEndTime(ZonedDateTime.now());
        trip.getPayment().setPrice(finalAmount);

        return TripInfoDto.builder()
                .driverPhoneNumber(appUser.getPhoneNumber())
                .tripId(tripId)
                .reward(trip.getPayment().getReward())
                .tripStatus(trip.getStatus().name())
                .paymentStatus(trip.getPayment().getStatus().name())
                .price(trip.getPayment().getPrice())
                .paymentCorrelationId(trip.getPayment().getCorrelationId())
                .build();
    }
}
