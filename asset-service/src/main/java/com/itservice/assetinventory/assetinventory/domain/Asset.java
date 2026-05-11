package com.itservice.assetinventory.assetinventory.domain;

import jakarta.persistence.*;
import lombok.*;

    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Asset {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Embedded
        private AssetIdentifier assetIdentifier;


        @Enumerated(EnumType.STRING)
        private AssetType type;

        @Enumerated(EnumType.STRING)
        private AssetStatus status;
    }

