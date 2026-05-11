package com.itservice.apigatewa.apigateway.presentationlayer;

import com.itservice.apigatewa.apigateway.businesslogiclayer.asset.AssetService;
import com.itservice.apigatewa.apigateway.businesslogiclayer.incident.TicketService;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetController;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetType;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.Priority;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketController;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketStatus;
import com.itservice.apigatewa.apigateway.utilities.GlobalControllerExceptionHandler;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GatewayControllerUnitTest {

    @Test
    void assetControllerUnit_returnsAssets() {
        AssetService assetService = mock(AssetService.class);
        when(assetService.getAllAssets()).thenReturn(List.of(assetResponse("AST-5001")));
        WebTestClient webTestClient = MockMvcWebTestClient.bindToController(new AssetController(assetService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();

        webTestClient.get()
                .uri("/api/v1/assets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].assetId").isEqualTo("AST-5001");
    }

    @Test
    void ticketControllerUnit_handlesNotFoundException() {
        TicketService ticketService = mock(TicketService.class);
        when(ticketService.getByTicketId("bad-id")).thenThrow(new NotFoundException("Ticket not found"));
        WebTestClient webTestClient = MockMvcWebTestClient.bindToController(new TicketController(ticketService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();

        webTestClient.get()
                .uri("/api/v1/tickets/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket not found");
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

    private TicketResponseDTO ticketResponse(String ticketId) {
        return TicketResponseDTO.builder()
                .ticketId(ticketId)
                .title("Laptop issue")
                .description("Laptop will not boot")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }
}
