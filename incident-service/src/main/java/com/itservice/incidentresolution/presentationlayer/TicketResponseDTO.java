package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDTO {
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
