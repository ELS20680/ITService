package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.utilities.AssetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssetDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.asset-service.base-url}")
    private String assetServiceBaseUrl;

    public AssetResponseDTO getAssetById(String assetId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(assetServiceBaseUrl + "/api/v1/assets/{id}", assetId)
                    .retrieve()
                    .bodyToMono(AssetResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new AssetNotFoundException("Asset with id " + assetId + " not found");
        }
    }
}
