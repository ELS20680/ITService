package com.itservice.incidentresolution.dataccesslayer;

import com.itservice.incidentresolution.domain.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    Optional<Ticket> findTicketByTicketIdentifier_TicketId(String ticketId);
    boolean existsByTicketIdentifier_TicketId(String ticketId);
}
