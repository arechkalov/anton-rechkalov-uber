package com.ar.uber.web.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class GenericResponse {

    private final Integer code;

    private final Object body;

}
