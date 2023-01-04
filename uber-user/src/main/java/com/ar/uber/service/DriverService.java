package com.ar.uber.service;

import com.ar.uber.dto.DriverProfileDto;
import com.ar.uber.web.request.VehicleCreateRequest;
import com.ar.uber.web.response.DailyAnalytics;

public interface DriverService {

    void update(Long id, VehicleCreateRequest vehicleCreateRequest); // use one object as input argument

    DriverProfileDto getStatistics(String phoneNumber);
    DailyAnalytics getAnalytics(String phoneNumber);
}
