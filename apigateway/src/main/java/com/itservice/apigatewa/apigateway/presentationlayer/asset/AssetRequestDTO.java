package com.itservice.apigatewa.apigateway.presentationlayer.asset;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetRequestDTO {
    @NotBlank(message="Need an AssetType")
    private AssetType type;

    @NotBlank(message="Need an AssetStatus")
    private AssetStatus status;
}
