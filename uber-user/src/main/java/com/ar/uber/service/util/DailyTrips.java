package com.ar.uber.service.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ar.uber.model.Trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class DailyTrips {

    private LocalDate day;

    private List<Trip> trips = new ArrayList<>();

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

}
