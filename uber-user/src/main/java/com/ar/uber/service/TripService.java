package com.ar.uber.service;

import java.math.BigDecimal;
import java.util.List;

import com.ar.uber.appuser.AppUser;
import com.ar.uber.dto.TripDto;
import com.ar.uber.model.DriverRating;
import com.ar.uber.web.request.TripCreateRequest;
import com.ar.uber.web.response.TripCreatedResponse;
import com.ar.uber.web.response.TripInfoDto;

public interface TripService {

    TripCreatedResponse add(TripCreateRequest tripCreateRequest); // response should be DTO for consistency

    List<TripDto> getAll(Long id);

    TripInfoDto acquireTrip(AppUser appUser, Long tripId);

    TripInfoDto confirmTrip(AppUser appUser, Long tripId, final BigDecimal finalAmount, final DriverRating driverRating);

    TripInfoDto abortTrip(final String paymentCorrelationId);
}
