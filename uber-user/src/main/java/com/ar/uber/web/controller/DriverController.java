package com.ar.uber.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class DriverController {

    private final DriverService driverService;

    @GetMapping(value = "{driverPhoneNumber}/statistics")
    public ResponseEntity<DriverProfileDto> getDriver(@PathVariable String driverPhoneNumber ) {
        DriverProfileDto driverProfileDto = driverService.getStatistics(driverPhoneNumber);
        return new ResponseEntity<>(driverProfileDto, OK);
    }

    @PutMapping(value = "vehicles")
    public ResponseEntity<Object> registerVehicle(@RequestBody VehicleCreateRequest vehicleCreateRequest) {
        final AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        driverService.update(appUser.getId(), vehicleCreateRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{driverPhoneNumber}/trips/analytics")
    public ResponseEntity<DailyAnalytics> getDailyAnalytics(@PathVariable String driverPhoneNumber) {
        DailyAnalytics analytics = driverService.getAnalytics(driverPhoneNumber);
        return ResponseEntity.ok(analytics);

    }
}
