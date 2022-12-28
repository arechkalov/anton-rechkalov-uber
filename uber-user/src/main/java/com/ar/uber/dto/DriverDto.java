package com.ar.uber.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DriverDto {

    private String firstName;

    private String lastName;

    private String phoneNumber;
}
