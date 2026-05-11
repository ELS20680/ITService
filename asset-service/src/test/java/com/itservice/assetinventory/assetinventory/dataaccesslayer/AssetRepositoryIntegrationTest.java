package com.itservice.assetinventory.assetinventory.dataaccesslayer;

import com.itservice.assetinventory.assetinventory.domain.Asset;
import com.itservice.assetinventory.assetinventory.domain.AssetIdentifier;
import com.itservice.assetinventory.assetinventory.domain.AssetStatus;
import com.itservice.assetinventory.assetinventory.domain.AssetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("h2")
class AssetRepositoryIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @Test
    void findAssetByAssetIdentifier_AssetId_withExistingAsset_returnsAsset() {
        Asset asset = Asset.builder()
                .assetIdentifier(new AssetIdentifier("AST-9001"))
                .type(AssetType.LAPTOP)
                .status(AssetStatus.IN_SERVICE)
                .build();
        assetRepository.save(asset);

        Asset foundAsset = assetRepository.findAssetByAssetIdentifier_AssetId("AST-9001");

        assertNotNull(foundAsset);
        assertEquals(AssetType.LAPTOP, foundAsset.getType());
    }

    @Test
    void findAssetByAssetIdentifier_AssetId_withMissingAsset_returnsNull() {
        Asset foundAsset = assetRepository.findAssetByAssetIdentifier_AssetId("AST-9999");

        assertNull(foundAsset);
    }
}
