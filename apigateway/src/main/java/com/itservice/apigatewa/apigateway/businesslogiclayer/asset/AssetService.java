package com.itservice.apigatewa.apigateway.businesslogiclayer.asset;

import com.itservice.apigatewa.apigateway.domainclientlayer.asset.AssetServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetController;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AssetService {
    private final AssetServiceClient assetServiceClient;

    public AssetService(AssetServiceClient assetServiceClient) {
        this.assetServiceClient = assetServiceClient;
    }

    public List<AssetResponseDTO> getAllAssets() {
        List<AssetResponseDTO> assetResponseDTOS = assetServiceClient.getAllAssets();
        if(assetResponseDTOS != null) {
            for (AssetResponseDTO assetResponseDTO : assetResponseDTOS) {
                addLinks(assetResponseDTO);
            }
        }
        return assetResponseDTOS;
    }

    public AssetResponseDTO getAssetById(String assetId) {
        AssetResponseDTO assetResponseDTO = assetServiceClient.getAssetById(assetId);
        if(assetResponseDTO != null) {
            addLinks(assetResponseDTO);
        }
        return assetResponseDTO;
    }

    public AssetResponseDTO createAsset(AssetRequestDTO assetRequestDTO) {
        AssetResponseDTO assetResponseDTO = assetServiceClient.createAsset(assetRequestDTO);
        if(assetResponseDTO != null) {
            addLinks(assetResponseDTO);
            return assetResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + assetRequestDTO);
    }

    public AssetResponseDTO updateAsset(String assetId, AssetRequestDTO assetRequestDTO) {
        AssetResponseDTO assetResponseDTO = assetServiceClient.updateAsset(assetId, assetRequestDTO);
        if(assetResponseDTO != null) {
            addLinks(assetResponseDTO);
            return assetResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + assetRequestDTO);
    }

    public void deleteAsset(String assetId) {
        assetServiceClient.deleteAsset(assetId);
    }

    private AssetResponseDTO addLinks(AssetResponseDTO assetResponseDTO) {
        Link self = linkTo(methodOn(AssetController.class).getAssetById(assetResponseDTO.getAssetId())).withSelfRel();
        Link all = linkTo(methodOn(AssetController.class).getAll()).withRel("All assets");
        return assetResponseDTO.add(all,self);
    }
}
