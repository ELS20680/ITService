package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import com.itservice.apigatewa.apigateway.businesslogiclayer.incident.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<TicketResponseDTO>> getAll() {
        return ResponseEntity.ok().body(ticketService.getAllTickets());
    }

    @GetMapping(
            value = "/{ticketId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketResponseDTO> getByTicketId(@PathVariable String ticketId) {
        return ResponseEntity.ok().body(ticketService.getByTicketId(ticketId));
    }

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO ticketRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(ticketRequestDTO));
    }

    @PutMapping(
            value = "/{ticketId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TicketResponseDTO> updateTicket(@RequestBody TicketRequestDTO ticketRequestDTO, @PathVariable String ticketId) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, ticketRequestDTO));
    }

    @DeleteMapping(
            value = "/{ticketId}"
    )
    public ResponseEntity<Void> deleteTicket(@PathVariable String ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
