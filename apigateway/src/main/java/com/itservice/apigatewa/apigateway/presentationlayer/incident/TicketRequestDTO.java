package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequestDTO {
    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "status is required")
    private TicketStatus status;

    @NotNull(message = "priority is required")
    private Priority priority;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "staffId is required")
    private String staffId;

    @NotBlank(message = "assetId is required")
    private String assetId;
}
