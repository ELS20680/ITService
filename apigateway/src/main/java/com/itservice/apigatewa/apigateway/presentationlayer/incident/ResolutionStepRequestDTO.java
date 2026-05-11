package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResolutionStepRequestDTO {
    @NotNull(message = "stepNumber is required")
    private Integer stepNumber;

    @NotBlank(message = "actionTaken is required")
    private String actionTaken;

    @NotBlank(message = "result is required")
    private String result;
}
