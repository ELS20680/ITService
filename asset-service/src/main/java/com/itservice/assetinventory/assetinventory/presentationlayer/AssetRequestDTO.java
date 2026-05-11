package com.itservice.assetinventory.assetinventory.presentationlayer;


import com.itservice.assetinventory.assetinventory.domain.AssetStatus;
import com.itservice.assetinventory.assetinventory.domain.AssetType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssetRequestDTO {
    @NotBlank(message="Need an AssetType")
 private AssetType type;

    @NotBlank(message="Need an AssetStatus")
    private AssetStatus status;
}




//
//@Data
//public class HandymanProfileRequestDTO {
//
//    @NotBlank(message = "First name is required")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    private String lastName;
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Email must be valid")
//    private String email;
//
//    @NotNull(message = "Work zone is required")
//    private WorkZone workZone;
//
//    private List<SkillSet> skillSets;
//
//    private AvailabilityCalendar availabilityCalendar;
//}