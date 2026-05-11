package com.itservice.incidentresolution.dataccesslayer;

import com.itservice.incidentresolution.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findTicketByTicketIdentifier_TicketId(String ticketId);
    boolean existsByTicketIdentifier_TicketId(String ticketId);
}
