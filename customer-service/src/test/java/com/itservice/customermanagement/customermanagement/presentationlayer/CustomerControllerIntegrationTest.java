package com.itservice.customermanagement.customermanagement.presentationlayer;

import com.itservice.customermanagement.customermanagement.dataaccesslayer.CustomerRepository;
import com.itservice.customermanagement.customermanagement.domain.Customer;
import com.itservice.customermanagement.customermanagement.domain.CustomerIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class CustomerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CustomerRepository customerRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
        customerRepository.save(Customer.builder()
                .customerIdentifier(new CustomerIdentifier("CUST-001"))
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.j@company.com")
                .department("Marketing")
                .build());

        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void getByCustomerId_withExistingCustomer_returnsCustomer() {
        webTestClient.get()
                .uri("/api/v1/customers/CUST-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo("CUST-001");
    }

    @Test
    void getByCustomerId_withMissingCustomer_returnsNotFound() {
        webTestClient.get()
                .uri("/api/v1/customers/CUST-9999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Customer not found : CUST-9999");
    }

    @Test
    void createCustomer_returnsCreatedCustomer() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Nadia");
        requestDTO.setLastName("White");
        requestDTO.setEmail("nadia.white@company.com");
        requestDTO.setDepartment("Finance");

        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.department").isEqualTo("Finance");
    }
}
