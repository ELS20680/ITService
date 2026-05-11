package com.itservice.apigatewa.apigateway.mappinglayer.asset;

import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetController;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AssetMapper {
//    public AssetResponseDTO toDTO(Asset asset) {
//        AssetResponseDTO assetResponseDTO = new AssetResponseDTO();
//        assetResponseDTO.setAssetId(asset.getAssetIdentifier().getAssetId());
//        assetResponseDTO.setType(asset.getType());
//        assetResponseDTO.setStatus(asset.getStatus());
//
//        Link all = linkTo(methodOn(AssetController.class).getAll()).withRel("all Assets");
//        assetResponseDTO.add(all);
//
//        return assetResponseDTO;
//    }
//
//    public Asset toEntity(AssetRequestDTO assetRequestDTO) {
//        Asset asset = new Asset();
//        asset.setType(assetRequestDTO.getType());
//        asset.setStatus(assetRequestDTO.getStatus());
//
//        return asset;
//    }
}
