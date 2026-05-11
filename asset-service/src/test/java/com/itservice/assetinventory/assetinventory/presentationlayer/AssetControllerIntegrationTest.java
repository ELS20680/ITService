package com.itservice.assetinventory.assetinventory.presentationlayer;

import com.itservice.assetinventory.assetinventory.dataaccesslayer.AssetRepository;
import com.itservice.assetinventory.assetinventory.domain.Asset;
import com.itservice.assetinventory.assetinventory.domain.AssetIdentifier;
import com.itservice.assetinventory.assetinventory.domain.AssetStatus;
import com.itservice.assetinventory.assetinventory.domain.AssetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AssetControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AssetRepository assetRepository;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        assetRepository.deleteAll();
        assetRepository.save(Asset.builder()
                .assetIdentifier(new AssetIdentifier("AST-5001"))
                .type(AssetType.LAPTOP)
                .status(AssetStatus.IN_SERVICE)
                .build());

        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void getByAssetId_withExistingAsset_returnsAsset() {
        webTestClient.get()
                .uri("/api/v1/assets/AST-5001")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.assetId").isEqualTo("AST-5001");
    }

    @Test
    void getByAssetId_withMissingAsset_returnsNotFound() {
        webTestClient.get()
                .uri("/api/v1/assets/AST-9999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Asset not found AST-9999");
    }

    @Test
    void createAsset_returnsCreatedAsset() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.DESKTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);

        webTestClient.post()
                .uri("/api/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.type").isEqualTo("DESKTOP");
    }
}
