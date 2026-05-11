package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.TicketStatus;
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
