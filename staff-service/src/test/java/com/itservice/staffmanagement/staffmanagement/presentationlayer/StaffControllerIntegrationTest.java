package com.itservice.staffmanagement.staffmanagement.presentationlayer;

import com.itservice.staffmanagement.staffmanagement.dataaccesslayer.StaffRepository;
import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import com.itservice.staffmanagement.staffmanagement.domain.StaffIdentifier;
import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class StaffControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private StaffRepository staffRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        staffRepository.deleteAll();
        staffRepository.save(Staff.builder()
                .staffIdentifier(new StaffIdentifier("STAFF-101"))
                .firstName("David")
                .lastName("Miller")
                .email("d.miller@it-support.com")
                .staffRole(StaffRole.AGENT)
                .build());

        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void getByStaffId_withExistingStaff_returnsStaff() {
        webTestClient.get()
                .uri("/api/v1/staff/STAFF-101")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.staffId").isEqualTo("STAFF-101");
    }

    @Test
    void getByStaffId_withMissingStaff_returnsNotFound() {
        webTestClient.get()
                .uri("/api/v1/staff/STAFF-9999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Staff not found STAFF-9999");
    }

    @Test
    void createStaff_returnsCreatedStaff() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("Sara");
        requestDTO.setLastName("Lee");
        requestDTO.setEmail("sara.lee@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);

        webTestClient.post()
                .uri("/api/v1/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.staffRole").isEqualTo("AGENT");
    }
}
