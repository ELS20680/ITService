package com.itservice.incidentresolution.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TicketIdentifier {
    private String ticketId;

    public TicketIdentifier() {
        this.ticketId = UUID.randomUUID().toString();
    }
}
