package com.itservice.incidentresolution.presentationlayer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionStepRequestDTO {

    @NotNull(message = "stepNumber is required")
    private Integer stepNumber;

    @NotBlank(message = "actionTaken is required")
    private String actionTaken;

    @NotBlank(message = "result is required")
    private String result;
}
