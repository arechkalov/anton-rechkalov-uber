package com.ar.uber.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.ar.uber.dto.TripDto;
import com.ar.uber.dto.TripSummary;
import com.ar.uber.model.Trip;
import com.ar.uber.payment.model.Payment;

@Service
public class TripConverter implements Converter<Trip, TripDto> {

    @Override
    public TripDto convert(@NonNull final Trip trip) {
       final Payment payment = trip.getPayment();

       return TripDto.builder()
               .tripSummary(TripSummary.builder()
                       .endLocation(trip.getEndLocation().toString())
                       .startLocation(trip.getStartLocation().toString())
                       .endTime(trip.getEndTime())
                       .startTime(trip.getStartTime())
                       .tripStatus(trip.getStatus().toString())
                       .paymentStatus(payment.getStatus().toString())
                       .paymentConfirmTime(payment.getConfirmTime())
                       .paymentInitiationTime(payment.getInitiationTime())
                       .reward(payment.getReward().toString())
                       .price(payment.getPrice().toString())
                       .build())
               .build();
    }
}
