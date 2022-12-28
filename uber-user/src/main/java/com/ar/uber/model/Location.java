package com.ar.uber.model;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@ToString
public class Location {
    private double longitude;
    private double latitude;

    public static Location toEntity(com.ar.uber.web.request.Location location) {
        return new Location(location.getLongitude(), location.getLongitude());
    }
}
