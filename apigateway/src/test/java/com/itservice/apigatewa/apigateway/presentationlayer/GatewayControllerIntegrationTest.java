package com.itservice.apigatewa.apigateway.presentationlayer;

import com.itservice.apigatewa.apigateway.domainclientlayer.asset.AssetServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.customer.CustomerServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.ResolutionStepServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.TicketServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.staff.StaffServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetType;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.Priority;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRole;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class GatewayControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AssetServiceClient assetServiceClient;

    @MockBean
    private CustomerServiceClient customerServiceClient;

    @MockBean
    private StaffServiceClient staffServiceClient;

    @MockBean
    private TicketServiceClient ticketServiceClient;

    @MockBean
    private ResolutionStepServiceClient resolutionStepServiceClient;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void assetEndpoints_useGatewayAndReturnResponses() {
        when(assetServiceClient.getAllAssets()).thenReturn(List.of(assetResponse("AST-5001")));
        when(assetServiceClient.getAssetById("AST-5001")).thenReturn(assetResponse("AST-5001"));
        when(assetServiceClient.createAsset(any(AssetRequestDTO.class))).thenReturn(assetResponse("AST-5002"));
        when(assetServiceClient.updateAsset(any(String.class), any(AssetRequestDTO.class))).thenReturn(assetResponse("AST-5001"));

        webTestClient.get().uri("/api/v1/assets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].assetId").isEqualTo("AST-5001");

        webTestClient.get().uri("/api/v1/assets/AST-5001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.assetId").isEqualTo("AST-5001")
                .jsonPath("$._links.self.href").exists();

        webTestClient.post().uri("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(assetRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.assetId").isEqualTo("AST-5002");

        webTestClient.put().uri("/api/v1/assets/AST-5001")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(assetRequest())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.assetId").isEqualTo("AST-5001");

        webTestClient.delete().uri("/api/v1/assets/AST-5001")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void customerAndStaffEndpoints_useGatewayAndReturnResponses() {
        when(customerServiceClient.getAllCustomers()).thenReturn(List.of(customerResponse("CUST-001")));
        when(customerServiceClient.getCustomerById("CUST-001")).thenReturn(customerResponse("CUST-001"));
        when(customerServiceClient.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponse("CUST-002"));
        when(customerServiceClient.updateCustomer(any(String.class), any(CustomerRequestDTO.class))).thenReturn(customerResponse("CUST-001"));

        when(staffServiceClient.getAllStaff()).thenReturn(List.of(staffResponse("STAFF-101")));
        when(staffServiceClient.getStaffById("STAFF-101")).thenReturn(staffResponse("STAFF-101"));
        when(staffServiceClient.createStaff(any(StaffRequestDTO.class))).thenReturn(staffResponse("STAFF-102"));
        when(staffServiceClient.updateStaff(any(String.class), any(StaffRequestDTO.class))).thenReturn(staffResponse("STAFF-101"));

        webTestClient.get().uri("/api/v1/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].customerId").isEqualTo("CUST-001");

        webTestClient.post().uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo("CUST-002");

        webTestClient.put().uri("/api/v1/customers/CUST-001")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequest())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo("CUST-001");

        webTestClient.get().uri("/api/v1/staff")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].staffId").isEqualTo("STAFF-101");

        webTestClient.post().uri("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(staffRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.staffId").isEqualTo("STAFF-102");

        webTestClient.delete().uri("/api/v1/staff/STAFF-101")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void incidentEndpoints_useGatewayAndReturnResponses() {
        when(ticketServiceClient.getAllTickets()).thenReturn(List.of(ticketResponse("TIC-1001")));
        when(ticketServiceClient.getByTicketId("TIC-1001")).thenReturn(ticketResponse("TIC-1001"));
        when(ticketServiceClient.createTicket(any(TicketRequestDTO.class))).thenReturn(ticketResponse("TIC-1002"));
        when(ticketServiceClient.updateTicket(any(String.class), any(TicketRequestDTO.class))).thenReturn(ticketResponse("TIC-1001"));

        when(resolutionStepServiceClient.getAllResolutionSteps()).thenReturn(List.of(resolutionStepResponse("STEP-1001")));
        when(resolutionStepServiceClient.getResolutionStepById("STEP-1001")).thenReturn(resolutionStepResponse("STEP-1001"));
        when(resolutionStepServiceClient.getResolutionStepsByTicketId("TIC-1001")).thenReturn(List.of(resolutionStepResponse("STEP-1001")));
        when(resolutionStepServiceClient.createResolutionStep(any(String.class), any(ResolutionStepRequestDTO.class)))
                .thenReturn(resolutionStepResponse("STEP-1002"));

        webTestClient.get().uri("/api/v1/tickets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].ticketId").isEqualTo("TIC-1001");

        webTestClient.get().uri("/api/v1/tickets/TIC-1001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ticketId").isEqualTo("TIC-1001");

        webTestClient.post().uri("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ticketRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.ticketId").isEqualTo("TIC-1002");

        webTestClient.put().uri("/api/v1/tickets/TIC-1001")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ticketRequest())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.ticketId").isEqualTo("TIC-1001");

        webTestClient.delete().uri("/api/v1/tickets/TIC-1001")
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/api/v1/resolution-step")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].stepId").isEqualTo("STEP-1001");

        webTestClient.post().uri("/api/v1/tickets/TIC-1001/resolution-step")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resolutionStepRequest())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.stepId").isEqualTo("STEP-1002");

        webTestClient.delete().uri("/api/v1/resolution-step/STEP-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void downstreamExceptions_areHandledByGateway() {
        when(ticketServiceClient.getByTicketId("bad-id"))
                .thenThrow(new NotFoundException("Ticket not found"));
        when(assetServiceClient.createAsset(any(AssetRequestDTO.class)))
                .thenThrow(new InvalidInputException("Invalid asset"));

        webTestClient.get().uri("/api/v1/tickets/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket not found");

        webTestClient.post().uri("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(assetRequest())
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid asset");
    }

    private AssetRequestDTO assetRequest() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.LAPTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);
        return requestDTO;
    }

    private AssetResponseDTO assetResponse(String assetId) {
        return AssetResponseDTO.builder()
                .assetId(assetId)
                .type(AssetType.LAPTOP)
                .status(AssetStatus.IN_SERVICE)
                .build();
    }

    private CustomerRequestDTO customerRequest() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.j@company.com");
        requestDTO.setDepartment("Marketing");
        return requestDTO;
    }

    private CustomerResponseDTO customerResponse(String customerId) {
        return CustomerResponseDTO.builder()
                .customerId(customerId)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.j@company.com")
                .department("Marketing")
                .build();
    }

    private StaffRequestDTO staffRequest() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("David");
        requestDTO.setLastName("Miller");
        requestDTO.setEmail("d.miller@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);
        return requestDTO;
    }

    private StaffResponseDTO staffResponse(String staffId) {
        return StaffResponseDTO.builder()
                .staffId(staffId)
                .firstName("David")
                .lastName("Miller")
                .staffEmail("d.miller@it-support.com")
                .staffRole(StaffRole.AGENT)
                .build();
    }

    private TicketRequestDTO ticketRequest() {
        TicketRequestDTO requestDTO = new TicketRequestDTO();
        requestDTO.setTitle("Laptop issue");
        requestDTO.setDescription("Laptop will not boot");
        requestDTO.setCustomerId("CUST-001");
        requestDTO.setStaffId("STAFF-101");
        requestDTO.setAssetId("AST-5001");
        requestDTO.setStatus(TicketStatus.NEW);
        requestDTO.setPriority(Priority.HIGH);
        return requestDTO;
    }

    private TicketResponseDTO ticketResponse(String ticketId) {
        return TicketResponseDTO.builder()
                .ticketId(ticketId)
                .title("Laptop issue")
                .description("Laptop will not boot")
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

    private ResolutionStepRequestDTO resolutionStepRequest() {
        ResolutionStepRequestDTO requestDTO = new ResolutionStepRequestDTO();
        requestDTO.setStepNumber(1);
        requestDTO.setActionTaken("Checked the device");
        requestDTO.setResult("Device needs repair");
        return requestDTO;
    }

    private ResolutionStepResponseDTO resolutionStepResponse(String stepId) {
        return ResolutionStepResponseDTO.builder()
                .stepId(stepId)
                .ticketId("TIC-1001")
                .stepNumber(1)
                .actionTaken("Checked the device")
                .result("Device needs repair")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
