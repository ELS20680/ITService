package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO extends RepresentationModel<TicketResponseDTO> {
    private String ticketId;
    private String title;
    private TicketStatus status;
    private Priority priority;
    private String description;

    private String customerId;
    private String customerFirstName;
    private String customerLastName;

    private String staffId;
    private String staffFirstName;
    private String staffLastName;

    private String assetId;
    private String assetType;
}
