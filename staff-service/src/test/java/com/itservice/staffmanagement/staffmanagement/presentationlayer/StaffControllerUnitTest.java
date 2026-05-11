package com.itservice.staffmanagement.staffmanagement.presentationlayer;

import com.itservice.staffmanagement.staffmanagement.businesslayer.StaffService;
import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import com.itservice.staffmanagement.staffmanagement.utilities.GlobalControllerExceptionHandler;
import com.itservice.staffmanagement.staffmanagement.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaffControllerUnitTest {

    private StaffService staffService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        staffService = mock(StaffService.class);
        webTestClient = MockMvcWebTestClient.bindToController(new StaffController(staffService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAll_returnsStaff() {
        when(staffService.getAllStaff()).thenReturn(List.of(response("STAFF-1001")));

        webTestClient.get()
                .uri("/api/v1/staff")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].staffId").isEqualTo("STAFF-1001");
    }

    @Test
    void getByStaffId_withMissingStaff_returnsNotFound() {
        when(staffService.getByStaffId("bad-id")).thenThrow(new NotFoundException("Staff not found bad-id"));

        webTestClient.get()
                .uri("/api/v1/staff/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Staff not found bad-id");
    }

    @Test
    void createStaff_returnsCreated() {
        when(staffService.createStaff(any(StaffRequestDTO.class))).thenReturn(response("STAFF-1001"));

        webTestClient.post()
                .uri("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.staffId").isEqualTo("STAFF-1001");
    }

    @Test
    void deleteStaff_returnsNoContent() {
        webTestClient.delete()
                .uri("/api/v1/staff/STAFF-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    private StaffRequestDTO request() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("David");
        requestDTO.setLastName("Miller");
        requestDTO.setEmail("d.miller@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);
        return requestDTO;
    }

    private StaffResponseDTO response(String staffId) {
        StaffResponseDTO responseDTO = new StaffResponseDTO();
        responseDTO.setStaffId(staffId);
        responseDTO.setFirstName("David");
        responseDTO.setLastName("Miller");
        responseDTO.setStaffEmail("d.miller@it-support.com");
        responseDTO.setStaffRole(StaffRole.AGENT);
        return responseDTO;
    }
}
