package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.StaffNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaffDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.staff-service.base-url}")
    private String staffServiceBaseUrl;

    public StaffResponseDTO getStaffById(String staffId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(staffServiceBaseUrl + "/api/v1/staff/{id}", staffId)
                    .retrieve()
                    .bodyToMono(StaffResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new StaffNotFoundException("Staff with id " + staffId + " not found");
        }
    }
}
