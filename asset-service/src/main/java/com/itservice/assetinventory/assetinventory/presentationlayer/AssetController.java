package com.itservice.assetinventory.assetinventory.presentationlayer;


import com.itservice.assetinventory.assetinventory.businesslayer.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ResponseEntity<List<AssetResponseDTO>> getAll() {
        List<AssetResponseDTO> response = assetService.getAllAssets();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<AssetResponseDTO> getByAssetId(@PathVariable String assetId) {
        AssetResponseDTO response = assetService.getByAssetId(assetId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AssetResponseDTO> createAsset(@RequestBody AssetRequestDTO assetRequestDTO) {
        AssetResponseDTO assetResponseDTO = assetService.createAsset(assetRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assetResponseDTO);
    }

    @PutMapping("/{assetId}")
    public ResponseEntity<AssetResponseDTO> updateAsset(@PathVariable String assetId, @RequestBody AssetRequestDTO assetRequestDTO) {
        AssetResponseDTO assetResponseDTO = assetService.updateAsset(assetId, assetRequestDTO);
        return ResponseEntity.ok(assetResponseDTO);
    }
    //delete
    @DeleteMapping("/{assetId}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String assetId) {
        assetService.deleteAsset(assetId);
        return ResponseEntity.noContent().build();
    }

}

