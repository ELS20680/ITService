package com.itservice.incidentresolution.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResolutionStepNotFoundException extends RuntimeException {
    public ResolutionStepNotFoundException(String message) {
        super(message);
    }
}
