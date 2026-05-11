package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.businesslogiclayer.TicketService;
import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.TicketStatus;
import com.itservice.incidentresolution.utilities.GlobalControllerExceptionHandler;
import com.itservice.incidentresolution.utilities.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TicketControllerUnitTest {

    private TicketService ticketService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        ticketService = mock(TicketService.class);
        webTestClient = MockMvcWebTestClient.bindToController(new TicketController(ticketService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllTickets_returnsTickets() {
        when(ticketService.getAllTickets()).thenReturn(List.of(response("TIC-1001")));

        webTestClient.get()
                .uri("/api/v1/tickets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].ticketId").isEqualTo("TIC-1001");
    }

    @Test
    void getByTicketId_withMissingTicket_returnsNotFound() {
        when(ticketService.getByTicketId("bad-id"))
                .thenThrow(new TicketNotFoundException("Ticket with id bad-id not found"));

        webTestClient.get()
                .uri("/api/v1/tickets/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket with id bad-id not found");
    }

    @Test
    void createTicket_returnsCreated() {
        when(ticketService.createTicket(any(TicketRequestDTO.class))).thenReturn(response("TIC-1001"));

        webTestClient.post()
                .uri("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.ticketId").isEqualTo("TIC-1001");
    }

    @Test
    void createTicket_withInvalidBody_returnsUnprocessableEntity() {
        TicketRequestDTO requestDTO = request();
        requestDTO.setTitle("");

        webTestClient.post()
                .uri("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("title is required");
    }

    @Test
    void deleteTicket_returnsNoContent() {
        webTestClient.delete()
                .uri("/api/v1/tickets/TIC-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    private TicketRequestDTO request() {
        return TicketRequestDTO.builder()
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }

    private TicketResponseDTO response(String ticketId) {
        return TicketResponseDTO.builder()
                .ticketId(ticketId)
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .customerFirstName("Alice")
                .customerLastName("Johnson")
                .staffId("STAFF-101")
                .staffFirstName("David")
                .staffLastName("Miller")
                .assetId("AST-5001")
                .assetType("LAPTOP")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }
}
