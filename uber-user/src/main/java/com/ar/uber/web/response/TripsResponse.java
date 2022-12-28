package com.ar.uber.web.response;

import java.util.List;

import com.ar.uber.dto.TripDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TripsResponse {
    private List<TripDto> trips;
}
