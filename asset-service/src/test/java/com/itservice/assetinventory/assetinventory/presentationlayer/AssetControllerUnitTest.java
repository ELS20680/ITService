package com.itservice.assetinventory.assetinventory.presentationlayer;

import com.itservice.assetinventory.assetinventory.businesslayer.AssetService;
import com.itservice.assetinventory.assetinventory.domain.AssetStatus;
import com.itservice.assetinventory.assetinventory.domain.AssetType;
import com.itservice.assetinventory.assetinventory.utilities.GlobalControllerExceptionHandler;
import com.itservice.assetinventory.assetinventory.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AssetControllerUnitTest {

    private AssetService assetService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        assetService = mock(AssetService.class);
        webTestClient = MockMvcWebTestClient.bindToController(new AssetController(assetService))
                .controllerAdvice(new GlobalControllerExceptionHandler())
                .build();
    }

    @Test
    void getAll_returnsAssets() {
        when(assetService.getAllAssets()).thenReturn(List.of(response("AST-1001")));

        webTestClient.get()
                .uri("/api/v1/assets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].assetId").isEqualTo("AST-1001");
    }

    @Test
    void getByAssetId_withMissingAsset_returnsNotFound() {
        when(assetService.getByAssetId("bad-id")).thenThrow(new NotFoundException("Asset not found bad-id"));

        webTestClient.get()
                .uri("/api/v1/assets/bad-id")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Asset not found bad-id");
    }

    @Test
    void createAsset_returnsCreated() {
        when(assetService.createAsset(any(AssetRequestDTO.class))).thenReturn(response("AST-1001"));

        webTestClient.post()
                .uri("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.assetId").isEqualTo("AST-1001");
    }

    @Test
    void deleteAsset_returnsNoContent() {
        webTestClient.delete()
                .uri("/api/v1/assets/AST-1001")
                .exchange()
                .expectStatus().isNoContent();
    }

    private AssetRequestDTO request() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.LAPTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);
        return requestDTO;
    }

    private AssetResponseDTO response(String assetId) {
        AssetResponseDTO responseDTO = new AssetResponseDTO();
        responseDTO.setAssetId(assetId);
        responseDTO.setType(AssetType.LAPTOP);
        responseDTO.setStatus(AssetStatus.IN_SERVICE);
        return responseDTO;
    }
}
