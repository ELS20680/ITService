package com.itservice.incidentresolution.presentationlayer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolutionStepResponseDTO {
    private String stepId;
    private String ticketId;
    private LocalDateTime createdAt;
    private Integer stepNumber;
    private String actionTaken;
    private String result;
}
