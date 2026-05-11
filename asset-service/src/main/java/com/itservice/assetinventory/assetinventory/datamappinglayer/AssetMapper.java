package com.itservice.assetinventory.assetinventory.datamappinglayer;


import com.itservice.assetinventory.assetinventory.domain.Asset;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetController;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetRequestDTO;
import com.itservice.assetinventory.assetinventory.presentationlayer.AssetResponseDTO;
import jakarta.validation.constraints.NotBlank;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AssetMapper {
    public AssetResponseDTO toDTO(Asset asset) {
        AssetResponseDTO assetResponseDTO = new AssetResponseDTO();
        assetResponseDTO.setAssetId(asset.getAssetIdentifier().getAssetId());
        assetResponseDTO.setType(asset.getType());
        assetResponseDTO.setStatus(asset.getStatus());

        Link all = linkTo(methodOn(AssetController.class).getAll()).withRel("all assets");
        assetResponseDTO.add(all);

        Link self = linkTo(methodOn(AssetController.class).getByAssetId(assetResponseDTO.getAssetId())).withSelfRel();
        assetResponseDTO.add(self);


        return assetResponseDTO;
    }

    public Asset toEntity(AssetRequestDTO assetRequestDTO) {
        Asset asset = new Asset();
        asset.setType(assetRequestDTO.getType());
        asset.setStatus(assetRequestDTO.getStatus());
        return asset;
    }
}
//
//@NotBlank(message="Need an AssetType")
//private AssetType type;
//
//@NotBlank(message="Need an AssetStatus")
//private AssetStatus status;
//}