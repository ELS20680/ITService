package com.itservice.staffmanagement.staffmanagement.presentationlayer;


import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponseDTO extends RepresentationModel<StaffResponseDTO> {
    private String staffId;
    private String firstName;
    private String lastName;
    private String staffEmail;
    private StaffRole staffRole;
}


//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AssetResponseDTO extends RepresentationModel<com.ericinc.midterm.assetinventory.presentationlayer.AssetResponseDTO> {
//    private String assetId;
//    private AssetType type;
//    private AssetStatus status;
//}