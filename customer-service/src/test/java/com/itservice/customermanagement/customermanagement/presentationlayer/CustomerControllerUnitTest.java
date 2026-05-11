package com.itservice.customermanagement.customermanagement.presentationlayer;

import com.itservice.customermanagement.customermanagement.businesslayer.CustomerService;
import com.itservice.customermanagement.customermanagement.utilities.GlobalControllerExceptionHandler;
import com.itservice.customermanagement.customermanagement.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerControllerUnitTest {

    private CustomerService customerService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        customerService = mock(CustomerService.class);
        webTestClient = MockMvcWebTestClient.bindToController(new CustomerController(customerService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAll_returnsCustomers() {
        when(customerService.getAllCustomers()).thenReturn(List.of(response("CUST-1001")));

        webTestClient.get()
                .uri("/api/v1/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].customerId").isEqualTo("CUST-1001");
    }

    @Test
    void getByCustomerId_withMissingCustomer_returnsNotFound() {
        when(customerService.getByCustomerId("bad-id")).thenThrow(new NotFoundException("Customer not found : bad-id"));

        webTestClient.get()
                .uri("/api/v1/customers/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Customer not found : bad-id");
    }

    @Test
    void createCustomer_returnsCreated() {
        when(customerService.createCustomer(any(CustomerRequestDTO.class))).thenReturn(response("CUST-1001"));

        webTestClient.post()
                .uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.customerId").isEqualTo("CUST-1001");
    }

    @Test
    void deleteCustomer_returnsNoContent() {
        webTestClient.delete()
                .uri("/api/v1/customers/CUST-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    private CustomerRequestDTO request() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.j@company.com");
        requestDTO.setDepartment("Engineering");
        return requestDTO;
    }

    private CustomerResponseDTO response(String customerId) {
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setCustomerId(customerId);
        responseDTO.setFirstName("Alice");
        responseDTO.setLastName("Johnson");
        responseDTO.setEmail("alice.j@company.com");
        responseDTO.setDepartment("Engineering");
        return responseDTO;
    }
}
