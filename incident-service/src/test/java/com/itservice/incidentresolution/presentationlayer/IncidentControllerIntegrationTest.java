package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.dataccesslayer.ResolutionStepRepository;
import com.itservice.incidentresolution.dataccesslayer.TicketRepository;
import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketIdentifier;
import com.itservice.incidentresolution.domain.TicketStatus;
import com.itservice.incidentresolution.domainclientlayer.AssetDomainClient;
import com.itservice.incidentresolution.domainclientlayer.CustomerDomainClient;
import com.itservice.incidentresolution.domainclientlayer.StaffDomainClient;
import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("testing-profile")
class IncidentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ResolutionStepRepository resolutionStepRepository;

    @MockBean
    private CustomerDomainClient customerDomainClient;

    @MockBean
    private StaffDomainClient staffDomainClient;

    @MockBean
    private AssetDomainClient assetDomainClient;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        resolutionStepRepository.deleteAll();
        ticketRepository.deleteAll();
        ticketRepository.save(ticket("TIC-1001", TicketStatus.NEW));
        ticketRepository.save(ticket("TIC-CLOSED", TicketStatus.CLOSED));
        mockClientResponses();

        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void getByTicketId_withExistingTicket_returnsAggregatedTicket() {
        webTestClient.get()
                .uri("/api/v1/tickets/TIC-1001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ticketId").isEqualTo("TIC-1001")
                .jsonPath("$.customerFirstName").isEqualTo("Alice")
                .jsonPath("$.assetType").isEqualTo("LAPTOP");
    }

    @Test
    void getByTicketId_withMissingTicket_returnsNotFound() {
        webTestClient.get()
                .uri("/api/v1/tickets/TIC-9999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket with id TIC-9999 not found");
    }

    @Test
    void createTicket_withValidReferences_returnsCreatedTicket() {
        webTestClient.post()
                .uri("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.customerFirstName").isEqualTo("Alice")
                .jsonPath("$.staffFirstName").isEqualTo("David");
    }

    @Test
    void createResolutionStep_withClosedTicket_returnsUnprocessableEntity() {
        ResolutionStepRequestDTO requestDTO = ResolutionStepRequestDTO.builder()
                .stepNumber(1)
                .actionTaken("Checked the device")
                .result("No action taken because ticket is closed")
                .build();

        webTestClient.post()
                .uri("/api/v1/tickets/TIC-CLOSED/resolution-step")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket with id TIC-CLOSED is already closed");
    }

    private void mockClientResponses() {
        when(customerDomainClient.getCustomerById("CUST-001")).thenReturn(
                CustomerResponseDTO.builder()
                        .customerId("CUST-001")
                        .firstName("Alice")
                        .lastName("Johnson")
                        .build()
        );
        when(staffDomainClient.getStaffById("STAFF-101")).thenReturn(
                StaffResponseDTO.builder()
                        .staffId("STAFF-101")
                        .firstName("David")
                        .lastName("Miller")
                        .build()
        );
        when(assetDomainClient.getAssetById("AST-5001")).thenReturn(
                AssetResponseDTO.builder()
                        .assetId("AST-5001")
                        .type("LAPTOP")
                        .status("IN_SERVICE")
                        .build()
        );
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

    private Ticket ticket(String ticketId, TicketStatus status) {
        return Ticket.builder()
                .ticketIdentifier(new TicketIdentifier(ticketId))
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(status)
                .priority(Priority.HIGH)
                .build();
    }
}
