package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.presentationlayer.TicketRequestDTO;
import com.itservice.incidentresolution.presentationlayer.TicketResponseDTO;

import java.util.List;

public interface TicketService {
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO getByTicketId(String ticketId);
    TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO);
    TicketResponseDTO updateTicket(String ticketId, TicketRequestDTO ticketRequestDTO);
    void deleteTicket(String ticketId);
}
