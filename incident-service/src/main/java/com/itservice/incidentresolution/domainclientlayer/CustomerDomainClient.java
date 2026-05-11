package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.utilities.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.customer-service.base-url}")
    private String customerServiceBaseUrl;

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
}
