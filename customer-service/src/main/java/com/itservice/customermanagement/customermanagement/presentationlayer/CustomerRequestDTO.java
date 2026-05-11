package com.itservice.customermanagement.customermanagement.presentationlayer;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String email;
    @NotBlank
    private String department;
}


//@Data
//public class AssetRequestDTO {
//    @NotBlank(message="Need an AssetType")
//    private AssetType type;
//
//    @NotBlank(message="Need an AssetStatus")
//    private AssetStatus status;
//}