package com.itservice.assetinventory.assetinventory.businesslayer;

import com.itservice.assetinventory.assetinventory.dataaccesslayer.AssetRepository;
import com.itservice.assetinventory.assetinventory.datamappinglayer.AssetMapper;
import com.itservice.assetinventory.assetinventory.domain.Asset;
import com.itservice.assetinventory.assetinventory.domain.AssetIdentifier;
import com.itservice.assetinventory.assetinventory.domain.AssetStatus;
import com.itservice.assetinventory.assetinventory.domain.AssetType;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetRequestDTO;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetResponseDTO;
import com.itservice.assetinventory.assetinventory.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceUnitTest {

    @Mock
    private AssetRepository assetRepository;

    private AssetService assetService;

    @BeforeEach
    void setup() {
        assetService = new AssetService(assetRepository, new AssetMapper());
    }

    @Test
    void getAllAssets_returnsMappedAssets() {
        when(assetRepository.findAll()).thenReturn(List.of(asset("AST-1001")));

        List<AssetResponseDTO> assets = assetService.getAllAssets();

        assertEquals(1, assets.size());
        assertEquals("AST-1001", assets.get(0).getAssetId());
    }

    @Test
    void getByAssetId_withExistingAsset_returnsAsset() {
        when(assetRepository.findAssetByAssetIdentifier_AssetId("AST-1001")).thenReturn(asset("AST-1001"));

        AssetResponseDTO asset = assetService.getByAssetId("AST-1001");

        assertEquals(AssetType.LAPTOP, asset.getType());
    }

    @Test
    void getByAssetId_withMissingAsset_throwsNotFoundException() {
        when(assetRepository.findAssetByAssetIdentifier_AssetId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> assetService.getByAssetId("bad-id"));
    }

    @Test
    void createAsset_savesAssetAndReturnsResponse() {
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset asset = invocation.getArgument(0);
            asset.setId(1L);
            return asset;
        });

        AssetResponseDTO response = assetService.createAsset(request());

        assertNotNull(response.getAssetId());
        assertEquals(AssetStatus.IN_SERVICE, response.getStatus());
    }

    @Test
    void updateAsset_withExistingAsset_keepsAssetId() {
        when(assetRepository.findAssetByAssetIdentifier_AssetId("AST-1001")).thenReturn(asset("AST-1001"));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssetResponseDTO response = assetService.updateAsset("AST-1001", request());

        assertEquals("AST-1001", response.getAssetId());
        assertEquals(AssetType.LAPTOP, response.getType());
    }

    @Test
    void deleteAsset_withMissingAsset_throwsNotFoundException() {
        when(assetRepository.findAssetByAssetIdentifier_AssetId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> assetService.deleteAsset("bad-id"));
    }

    @Test
    void deleteAsset_withExistingAsset_deletesAsset() {
        Asset asset = asset("AST-1001");
        when(assetRepository.findAssetByAssetIdentifier_AssetId("AST-1001")).thenReturn(asset);

        assetService.deleteAsset("AST-1001");

        verify(assetRepository).delete(asset);
    }

    private Asset asset(String assetId) {
        return Asset.builder()
                .id(1L)
                .assetIdentifier(new AssetIdentifier(assetId))
                .type(AssetType.LAPTOP)
                .status(AssetStatus.IN_SERVICE)
                .build();
    }

    private AssetRequestDTO request() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.LAPTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);
        return requestDTO;
    }
}
