package com.ar.uber.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

import javax.persistence.EntityManager;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ar.uber.dao.DriverRepository;
import com.ar.uber.dto.DriverDto;
import com.ar.uber.dto.DriverProfileDto;
import com.ar.uber.model.Driver;
import com.ar.uber.model.Trip;
import com.ar.uber.model.TripStatus;
import com.ar.uber.model.Vehicle;
import com.ar.uber.service.DriverService;
import com.ar.uber.service.util.DailyTrips;
import com.ar.uber.web.exception.EntityNotFoundException;
import com.ar.uber.web.exception.TripNotFoundForCurrentUserException;
import com.ar.uber.web.request.VehicleCreateRequest;
import com.ar.uber.web.response.DailyAnalytics;
import com.ar.uber.web.response.Day;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void update(final Long id, final VehicleCreateRequest vehicleCreateRequest) {
        final Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Driver id not found"));
        final Vehicle vehicle = Vehicle.builder()
                .color(vehicleCreateRequest.getColour())
                .name(vehicleCreateRequest.getName())
                .registeredAt(ZonedDateTime.now())
                .registrationNumber(vehicleCreateRequest.getRegistrationNumber())
                .build();
        entityManager.persist(vehicle);
        driver.setVehicle(vehicle);
    }

    @Override
    public DriverProfileDto getStatistics(final String phoneNumber) {
        Driver driver = driverRepository.findByAppUserPhoneNumber(phoneNumber)
                .orElseThrow(EntityNotFoundException::new);

        List<Trip> finishedTrips = driver.getTrips().stream()
                .filter(trip -> trip.getStatus().equals(TripStatus.COMPLETED))
                .collect(toList());

        Float ratingSum = finishedTrips.stream()
                .map(trip -> trip.getDriverRating().getRating())
                .reduce(0F, Float::sum);
        BigDecimal tripCostSum = finishedTrips.stream()
                .map(trip -> trip.getPayment().getPrice().add(trip.getPayment().getReward()))
                .reduce(BigDecimal.ZERO, (BigDecimal::add));

        float averageRating = 0F;
        BigDecimal averageTripCost = BigDecimal.ZERO;
        if (finishedTrips.size() > 0) {
            averageRating = ratingSum / finishedTrips.size();
            averageTripCost = tripCostSum.divide(BigDecimal.valueOf(finishedTrips.size()), 2, RoundingMode.HALF_EVEN);
        }

        Vehicle vehicle = driver.getVehicle();

        return DriverProfileDto.builder()
                .driver(DriverDto.builder()
                        .phoneNumber(phoneNumber)
                        .firstName(driver.getAppUser().getFirstName())
                        .lastName(driver.getAppUser().getLastName())
                        .build())
                .avgTripPrice(averageTripCost)
                .ratingScore(averageRating)
                .vehicles(Collections.singleton(vehicle))
                .build();//TODO use audit history with hibernate envers
    }

    @Override
    public DailyAnalytics getAnalytics(final String phoneNumber) {
        final Driver driver = driverRepository.findByAppUserPhoneNumber(phoneNumber)
                .orElseThrow(EntityNotFoundException::new);
        List<Trip> trips = driver.getTrips();
        final List<Trip> sortedList = getSortedTrips(trips);
        final List<DailyTrips> days = getSortedTripsPerDay(sortedList);
        final List<Day> daysDto = buildDaysDto(days);
        final BigDecimal totalPriceForAllTrips = getTotalPriceForAllTrips(sortedList);

        return DailyAnalytics.builder()
                .days(daysDto)
                .averageCostPerTrip(totalPriceForAllTrips.divide(BigDecimal.valueOf(sortedList.size()), 2, RoundingMode.HALF_EVEN))
                .build();
    }

    private BigDecimal getTotalPriceForAllTrips(final List<Trip> sortedList) {
        return sortedList.stream()
                .map(t -> t.getPayment().getPrice().add(t.getPayment().getReward()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Day> buildDaysDto(final List<DailyTrips> days) {
        final List<Day> daysDto = new ArrayList<>();
        for (final DailyTrips day : days) {
            final List<Trip> tripsPerDay = day.getTrips();
            final BigDecimal totalPaidAmountPerDay = getTotalPriceForAllTrips(tripsPerDay);
            int totalTripsPerDay = tripsPerDay.size();
            final LocalDate date = day.getDay();
            final Duration totalDurationOfTripsPerDay = tripsPerDay.stream()
                    .map(t -> Duration.between(t.getEndTime(), t.getStartTime()))
                    .reduce(Duration.ZERO, Duration::plus);

            daysDto.add(Day.builder()
                    .totalTripsTime(totalDurationOfTripsPerDay.toString()) //TODO format
                    .date(date)
                    .totalTripsPerDay(totalTripsPerDay)
                    .totalTripsPricePerDay(totalPaidAmountPerDay)
                    .build());
        }
        return daysDto;
    }

    private List<DailyTrips> getSortedTripsPerDay(final List<Trip> sortedList) {
        final Trip first = sortedList.stream().findFirst()
                .orElseThrow(TripNotFoundForCurrentUserException::new);
        LocalDate previousTripDate = first.getEndTime().toLocalDate();
        final List<DailyTrips> days = new ArrayList<>();
        for (final Trip trip : sortedList) {
            final LocalDate nextTripDate = trip.getEndTime().toLocalDate();
            if (previousTripDate.equals(nextTripDate)) {
                if (days.isEmpty()) {
                    DailyTrips dailyTrips = new DailyTrips();
                    dailyTrips.addTrip(trip);
                    dailyTrips.setDay(previousTripDate);
                    days.add(dailyTrips);

                } else {
                    // get last updated element and add trip
                    days.get(days.size() - 1).getTrips().add(trip);
                }
            } else {
                previousTripDate = nextTripDate;
                List<Trip> nextTripList = new ArrayList<>();
                nextTripList.add(trip);
                days.add(new DailyTrips(previousTripDate, nextTripList));
            }
        }
        return days;
    }

    private List<Trip> getSortedTrips(final List<Trip> trips) {
        return trips.stream()
                .filter(trip -> trip.getStatus().equals(TripStatus.COMPLETED))
                .sorted(Comparator.comparing(Trip::getEndTime))
                .collect(toList());
    }
}
