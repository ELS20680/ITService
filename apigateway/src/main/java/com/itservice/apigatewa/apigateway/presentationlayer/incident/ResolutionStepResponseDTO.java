package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResolutionStepResponseDTO extends RepresentationModel<ResolutionStepResponseDTO> {
    private String stepId;
    private String ticketId;
    private LocalDateTime createdAt;
    private Integer stepNumber;
    private String actionTaken;
    private String result;
}
