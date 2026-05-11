package com.itservice.staffmanagement.staffmanagement.presentationlayer;


import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class StaffRequestDTO {
    @NotBlank(message = "need staff fist name")
    private String fistName;
    @NotBlank(message = "need staff last name")
    private String lastName;
    @NotBlank(message = "need staff email")
    private String email;
    @NotBlank(message = "need staff role")
    private StaffRole staffRole;
}


//@Data
//public class AssetRequestDTO {
//    @NotBlank(message="Need an AssetType")
//    private AssetType type;
//
//    @NotBlank(message="Need an AssetStatus")
//    private AssetStatus status;
//}
