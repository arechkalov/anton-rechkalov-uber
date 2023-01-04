package com.ar.uber.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

import com.ar.uber.appuser.AppUser;
import com.ar.uber.dto.DriverProfileDto;
import com.ar.uber.service.DriverService;
import com.ar.uber.web.request.VehicleCreateRequest;
import com.ar.uber.web.response.DailyAnalytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Slf4j
// Add swagger and java documentations for all public methods
//Use @ControllerAdvice and create custom ApiError.class and Http.Status response for negative scenarios such as EntityNotFound, UserNotFound etc. exceptions.
public class DriverController {

    private final DriverService driverService;

    @GetMapping(value = "{driverPhoneNumber}/statistics")
    public ResponseEntity<DriverProfileDto> getDriver(@PathVariable String driverPhoneNumber ) { // create and wrap return type into a GetDriverProfileResponse class.
        DriverProfileDto driverProfileDto = driverService.getStatistics(driverPhoneNumber);
        return new ResponseEntity<>(driverProfileDto, OK);
    }

    @PutMapping(value = "vehicles")
    // rename VehicleCreateRequest => VehicleCreateRequest
    public ResponseEntity<?> registerVehicle(@RequestBody VehicleCreateRequest vehicleCreateRequest) { //use responseStatus created annotation instead of responseEntity and make method void.
        final AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        driverService.update(appUser.getId(), vehicleCreateRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{driverPhoneNumber}/trips/analytics")
    public ResponseEntity<DailyAnalytics> getDailyAnalytics(@PathVariable String driverPhoneNumber) { // create and wrap return type into a GetDailyAnalyticsResponse class.
        DailyAnalytics analytics = driverService.getAnalytics(driverPhoneNumber);
        return ResponseEntity.ok(analytics);

    }
}
