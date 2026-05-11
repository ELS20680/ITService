package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerRequestDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.utilities.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.customer-service.base-url}")
    private String customerServiceBaseUrl;

    public List<CustomerResponseDTO> getAllCustomers() {
        return webClientBuilder.build()
                .get()
                .uri(customerServiceBaseUrl + "/api/v1/customers")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CustomerResponseDTO>>() {})
                .block();
    }

    public CustomerResponseDTO getCustomerById(String customerId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(customerServiceBaseUrl + "/api/v1/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(CustomerResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
        }
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(customerServiceBaseUrl + "/api/v1/customers")
                .bodyValue(customerRequestDTO)
                .retrieve()
                .bodyToMono(CustomerResponseDTO.class)
                .block();
    }

    public CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO customerRequestDTO) {
        try {
            return webClientBuilder.build()
                    .put()
                    .uri(customerServiceBaseUrl + "/api/v1/customers/{id}", customerId)
                    .bodyValue(customerRequestDTO)
                    .retrieve()
                    .bodyToMono(CustomerResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
        }
    }

    public void deleteCustomer(String customerId) {
        try {
            webClientBuilder.build()
                    .delete()
                    .uri(customerServiceBaseUrl + "/api/v1/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new CustomerNotFoundException("Customer with id " + customerId + " not found");
        }
    }
}
