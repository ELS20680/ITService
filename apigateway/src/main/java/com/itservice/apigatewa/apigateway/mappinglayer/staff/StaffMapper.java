package com.itservice.apigatewa.apigateway.mappinglayer.staff;

import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffController;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffResponseDTO;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StaffMapper {
//    public StaffResponseDTO toDTO(Staff staff) {
//        StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
//        staffResponseDTO.setStaffId(staff.getStaffIdentifier().getStaffId());
//        staffResponseDTO.setFirstName(staff.getFirstName());
//        staffResponseDTO.setLastName(staff.getLastName());
//        staffResponseDTO.setStaffEmail(staff.getStaffEmail());
//        staffResponseDTO.setStaffRole(staff.getStaffRole());
//
//        Link all = linkTo(methodOn(StaffController.class).getAll()).withRel("all Staff");
//        staffResponseDTO.add(all);
//
//        return staffResponseDTO;
//    }
//
//    public Staff toEntity(StaffRequestDTO staffRequestDTO) {
//        Staff staff = new Staff();
//        staff.setFirstName(staffRequestDTO.getFistName());
//        staff.setLastName(staffRequestDTO.getLastName());
//        staff.setStaffEmail(staffRequestDTO.getEmail());
//        staff.setStaffRole(staffRequestDTO.getStaffRole());
//
//        return staff;
//    }
}
