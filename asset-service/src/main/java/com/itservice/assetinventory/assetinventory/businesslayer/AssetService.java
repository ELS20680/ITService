package com.itservice.assetinventory.assetinventory.businesslayer;

import com.itservice.assetinventory.assetinventory.dataaccesslayer.AssetRepository;
import com.itservice.assetinventory.assetinventory.datamappinglayer.AssetMapper;

import com.itservice.assetinventory.assetinventory.domain.Asset;
import com.itservice.assetinventory.assetinventory.domain.AssetIdentifier;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetController;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetRequestDTO;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetResponseDTO;

import com.itservice.assetinventory.assetinventory.utilities.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {
    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }


    public List<AssetResponseDTO> getAllAssets()
    {
     return assetRepository.findAll().stream()
             .map(assetMapper::toDTO)
             .collect(Collectors.toList());
    }

    public AssetResponseDTO getByAssetId(String assetId) {
        Asset asset = assetRepository.findAssetByAssetIdentifier_AssetId(assetId);
        if (asset == null) {
            throw new NotFoundException("Asset not found "+assetId);
        }
        return assetMapper.toDTO(asset);
    }

    public AssetResponseDTO createAsset(AssetRequestDTO assetRequestDTO) {
        AssetIdentifier assetIdentifier = new AssetIdentifier();
        Asset asset = assetMapper.toEntity(assetRequestDTO);
        asset.setAssetIdentifier(assetIdentifier);
        Asset persistedAsset = assetRepository.save(asset);
        return assetMapper.toDTO(persistedAsset);
    }

    public AssetResponseDTO updateAsset(String assetId, AssetRequestDTO assetRequestDTO){
        Asset asset = assetRepository.findAssetByAssetIdentifier_AssetId(assetId);
               if(asset== null){
                   throw new NotFoundException("Asset not found "+assetId);
               }

        Asset requestAsset = assetMapper.toEntity(assetRequestDTO);
        requestAsset.setId(asset.getId());
        requestAsset.setAssetIdentifier(asset.getAssetIdentifier());
        Asset persistedAsset = assetRepository.save(requestAsset);
        return assetMapper.toDTO(persistedAsset);
    }

    public void deleteAsset(String assetId) {
        Asset asset = assetRepository.findAssetByAssetIdentifier_AssetId(assetId);
        if (asset == null) {
            throw new NotFoundException("Asset not found "+assetId);
        }
        assetRepository.delete(asset);
    }

}