package com.itservice.apigatewa.apigateway.presentationlayer.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetResponseDTO extends RepresentationModel<AssetResponseDTO> {
    private String assetId;
    private AssetType type;
    private AssetStatus status;
}
