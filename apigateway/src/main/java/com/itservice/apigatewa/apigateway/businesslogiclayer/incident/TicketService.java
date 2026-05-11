package com.itservice.apigatewa.apigateway.businesslogiclayer.incident;

import com.itservice.apigatewa.apigateway.domainclientlayer.incident.TicketServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketController;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TicketService {
    private final TicketServiceClient ticketServiceClient;

    public TicketService(TicketServiceClient ticketServiceClient) {
        this.ticketServiceClient = ticketServiceClient;
    }

    public List<TicketResponseDTO> getAllTickets() {
        List<TicketResponseDTO> ticketResponseDTOS = ticketServiceClient.getAllTickets();
        if(ticketResponseDTOS != null) {
            for (TicketResponseDTO ticketResponseDTO : ticketResponseDTOS) {
                addLinks(ticketResponseDTO);
            }
        }
        return ticketResponseDTOS;
    }

    public TicketResponseDTO getByTicketId(String ticketId) {
        TicketResponseDTO ticketResponseDTO = ticketServiceClient.getByTicketId(ticketId);
        if(ticketResponseDTO != null) {
            addLinks(ticketResponseDTO);
        }
        return ticketResponseDTO;
    }

    public TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO) {
        TicketResponseDTO ticketResponseDTO = ticketServiceClient.createTicket(ticketRequestDTO);
        if(ticketResponseDTO != null) {
            addLinks(ticketResponseDTO);
            return ticketResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + ticketRequestDTO);
    }

    public TicketResponseDTO updateTicket(String ticketId, TicketRequestDTO ticketRequestDTO) {
        TicketResponseDTO ticketResponseDTO = ticketServiceClient.updateTicket(ticketId, ticketRequestDTO);
        if(ticketResponseDTO != null) {
            addLinks(ticketResponseDTO);
            return ticketResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + ticketRequestDTO);
    }

    public void deleteTicket(String ticketId) {
        ticketServiceClient.deleteTicket(ticketId);
    }

    private TicketResponseDTO addLinks(TicketResponseDTO ticketResponseDTO) {
        Link self = linkTo(methodOn(TicketController.class).getByTicketId(ticketResponseDTO.getTicketId())).withSelfRel();
        Link all = linkTo(methodOn(TicketController.class).getAll()).withRel("All tickets");
        return ticketResponseDTO.add(all, self);
    }
}
