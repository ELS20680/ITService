package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetRequestDTO;
import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.utilities.AssetNotFoundException;
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
public class AssetDomainClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${microservices.asset-service.base-url}")
    private String assetServiceBaseUrl;

    public List<AssetResponseDTO> getAllAssets() {
        return webClientBuilder.build()
                .get()
                .uri(assetServiceBaseUrl + "/api/v1/assets")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AssetResponseDTO>>() {})
                .block();
    }

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

    public AssetResponseDTO createAsset(AssetRequestDTO assetRequestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(assetServiceBaseUrl + "/api/v1/assets")
                .bodyValue(assetRequestDTO)
                .retrieve()
                .bodyToMono(AssetResponseDTO.class)
                .block();
    }

    public AssetResponseDTO updateAsset(String assetId, AssetRequestDTO assetRequestDTO) {
        try {
            return webClientBuilder.build()
                    .put()
                    .uri(assetServiceBaseUrl + "/api/v1/assets/{id}", assetId)
                    .bodyValue(assetRequestDTO)
                    .retrieve()
                    .bodyToMono(AssetResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new AssetNotFoundException("Asset with id " + assetId + " not found");
        }
    }

    public void deleteAsset(String assetId) {
        try {
            webClientBuilder.build()
                    .delete()
                    .uri(assetServiceBaseUrl + "/api/v1/assets/{id}", assetId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new AssetNotFoundException("Asset with id " + assetId + " not found");
        }
    }
}
