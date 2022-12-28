package com.ar.uber.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleCreateRequest {

    private String name;
    private String colour;
    private String registrationNumber;

}
