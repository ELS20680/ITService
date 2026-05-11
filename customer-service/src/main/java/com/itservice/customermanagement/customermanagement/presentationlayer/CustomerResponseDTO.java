package com.itservice.customermanagement.customermanagement.presentationlayer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
}


//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AssetResponseDTO extends RepresentationModel<com.ericinc.midterm.assetinventory.presentationlayer.AssetResponseDTO> {
//    private String assetId;
//    private AssetType type;
//    private AssetStatus status;
//}