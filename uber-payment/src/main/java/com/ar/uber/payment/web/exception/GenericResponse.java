package com.ar.uber.payment.web.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GenericResponse {

    private final String status;

    private final String message;

}
