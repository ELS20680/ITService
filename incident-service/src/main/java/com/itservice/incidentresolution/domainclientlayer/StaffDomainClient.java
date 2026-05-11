package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffRequestDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.StaffNotFoundException;
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
public class StaffDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.staff-service.base-url}")
    private String staffServiceBaseUrl;

    public List<StaffResponseDTO> getAllStaff() {
        return webClientBuilder.build()
                .get()
                .uri(staffServiceBaseUrl + "/api/v1/staff")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<StaffResponseDTO>>() {})
                .block();
    }

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

    public StaffResponseDTO createStaff(StaffRequestDTO staffRequestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(staffServiceBaseUrl + "/api/v1/staff")
                .bodyValue(staffRequestDTO)
                .retrieve()
                .bodyToMono(StaffResponseDTO.class)
                .block();
    }

    public StaffResponseDTO updateStaff(String staffId, StaffRequestDTO staffRequestDTO) {
        try {
            return webClientBuilder.build()
                    .put()
                    .uri(staffServiceBaseUrl + "/api/v1/staff/{id}", staffId)
                    .bodyValue(staffRequestDTO)
                    .retrieve()
                    .bodyToMono(StaffResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new StaffNotFoundException("Staff with id " + staffId + " not found");
        }
    }

    public void deleteStaff(String staffId) {
        try {
            webClientBuilder.build()
                    .delete()
                    .uri(staffServiceBaseUrl + "/api/v1/staff/{id}", staffId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new StaffNotFoundException("Staff with id " + staffId + " not found");
        }
    }
}
