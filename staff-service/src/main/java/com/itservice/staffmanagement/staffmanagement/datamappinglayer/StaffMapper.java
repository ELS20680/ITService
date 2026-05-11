package com.itservice.staffmanagement.staffmanagement.datamappinglayer;


import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffController;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffRequestDTO;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffResponseDTO;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Component
public class StaffMapper {
    public StaffResponseDTO toDTO(Staff staff){
        StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
        staffResponseDTO.setStaffId(staff.getStaffIdentifier().getStaffId());
        staffResponseDTO.setStaffEmail(staff.getEmail());
        staffResponseDTO.setFirstName(staff.getFirstName());
        staffResponseDTO.setLastName(staff.getLastName());
        staffResponseDTO.setStaffRole(staff.getStaffRole());

        Link all = linkTo(methodOn(StaffController.class).getAll()).withRel("all Staffs");
        staffResponseDTO.add(all);

        return staffResponseDTO;
    }

    public Staff toEntity(StaffRequestDTO staffRequestDTO) {
        Staff staff = new Staff();
        staff.setFirstName(staffRequestDTO.getFistName());
        staff.setLastName(staffRequestDTO.getLastName());
        staff.setStaffRole(staffRequestDTO.getStaffRole());
        staff.setEmail(staffRequestDTO.getEmail());
        return staff;
    }
}



//@Component
//public class AssetMapper {
//    public AssetResponseDTO toDTO(Asset asset) {
//        AssetResponseDTO assetResponseDTO = new AssetResponseDTO();
//        assetResponseDTO.setAssetId(asset.getAssetIdentifier().getAssetId());
//        assetResponseDTO.setType(asset.getType());
//        assetResponseDTO.setStatus(asset.getStatus());
//
//        Link all = linkTo(methodOn(AssetController.class).getAll()).withRel("all assets");
//        assetResponseDTO.add(all);
//
//        return assetResponseDTO;
//    }
//}