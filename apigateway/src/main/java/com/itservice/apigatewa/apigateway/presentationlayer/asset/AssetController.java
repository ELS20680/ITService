package com.itservice.apigatewa.apigateway.presentationlayer.asset;

import com.itservice.apigatewa.apigateway.businesslogiclayer.asset.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService){
        this.assetService = assetService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<AssetResponseDTO>> getAll(){
        return ResponseEntity.ok().body(assetService.getAllAssets());
    }

    @GetMapping(
            value = "/{assetId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AssetResponseDTO> getAssetById(@PathVariable String assetId){
        return ResponseEntity.ok().body(assetService.getAssetById(assetId));
    }

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AssetResponseDTO> createAsset(@RequestBody AssetRequestDTO assetRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.createAsset(assetRequestDTO));
    }

    @PutMapping(
            value = "/{assetId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AssetResponseDTO> updateAsset(@RequestBody AssetRequestDTO assetRequestDTO, @PathVariable String assetId){
        return ResponseEntity.ok(assetService.updateAsset(assetId, assetRequestDTO));
    }

    @DeleteMapping(
            value = "/{assetId}"
    )
    public ResponseEntity<Void> deleteAsset(@PathVariable String assetId) {
        assetService.deleteAsset(assetId);
        return ResponseEntity.noContent().build();
    }
}
