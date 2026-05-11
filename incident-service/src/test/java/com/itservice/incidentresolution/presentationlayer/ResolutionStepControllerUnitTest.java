package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.businesslogiclayer.ResolutionStepService;
import com.itservice.incidentresolution.utilities.GlobalControllerExceptionHandler;
import com.itservice.incidentresolution.utilities.ResolutionStepNotFoundException;
import com.itservice.incidentresolution.utilities.TicketAlreadyClosedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResolutionStepControllerUnitTest {

    private ResolutionStepService resolutionStepService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        resolutionStepService = mock(ResolutionStepService.class);
        webTestClient = MockMvcWebTestClient.bindToController(new ResolutionStepController(resolutionStepService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAllResolutionSteps_returnsSteps() {
        when(resolutionStepService.getAllResolutionSteps()).thenReturn(List.of(response("STEP-1001")));

        webTestClient.get()
                .uri("/api/v1/resolution-step")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].stepId").isEqualTo("STEP-1001");
    }

    @Test
    void getResolutionStepById_withMissingStep_returnsNotFound() {
        when(resolutionStepService.getResolutionStepById("bad-id"))
                .thenThrow(new ResolutionStepNotFoundException("Resolution step with id bad-id not found"));

        webTestClient.get()
                .uri("/api/v1/resolution-step/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Resolution step with id bad-id not found");
    }

    @Test
    void createResolutionStep_returnsCreated() {
        when(resolutionStepService.createResolutionStep(any(String.class), any(ResolutionStepRequestDTO.class)))
                .thenReturn(response("STEP-1001"));

        webTestClient.post()
                .uri("/api/v1/tickets/TIC-1001/resolution-step")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.stepId").isEqualTo("STEP-1001");
    }

    @Test
    void createResolutionStep_withClosedTicket_returnsUnprocessableEntity() {
        when(resolutionStepService.createResolutionStep(any(String.class), any(ResolutionStepRequestDTO.class)))
                .thenThrow(new TicketAlreadyClosedException("Ticket with id TIC-1001 is already closed"));

        webTestClient.post()
                .uri("/api/v1/tickets/TIC-1001/resolution-step")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Ticket with id TIC-1001 is already closed");
    }

    @Test
    void deleteResolutionStep_returnsNoContent() {
        webTestClient.delete()
                .uri("/api/v1/resolution-step/STEP-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    private ResolutionStepRequestDTO request() {
        return ResolutionStepRequestDTO.builder()
                .stepNumber(1)
                .actionTaken("Checked the device")
                .result("Device needs repair")
                .build();
    }

    private ResolutionStepResponseDTO response(String stepId) {
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
