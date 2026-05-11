package com.itservice.incidentresolution.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ticket {

    @Id
    private String id;

    private TicketIdentifier ticketIdentifier;

    private String title;

    private String description;

    private String customerId;

    private String staffId;

    private String assetId;

    private TicketStatus status;

    private Priority priority;
}
