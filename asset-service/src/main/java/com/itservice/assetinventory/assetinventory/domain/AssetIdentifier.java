package com.itservice.assetinventory.assetinventory.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class AssetIdentifier {
    private String assetId;

    public AssetIdentifier() {
        this.assetId = UUID.randomUUID().toString();
    }
}