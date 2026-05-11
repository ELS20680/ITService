package com.itservice.apigatewa.apigateway.businesslogiclayer.staff;

import com.itservice.apigatewa.apigateway.domainclientlayer.staff.StaffServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffController;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class StaffService {
    private final StaffServiceClient staffServiceClient;

    public StaffService(StaffServiceClient staffServiceClient) {
        this.staffServiceClient = staffServiceClient;
    }

    public List<StaffResponseDTO> getAllStaff() {
        List<StaffResponseDTO> staffResponseDTOS = staffServiceClient.getAllStaff();
        if(staffResponseDTOS != null) {
            for (StaffResponseDTO staffResponseDTO : staffResponseDTOS) {
                addLinks(staffResponseDTO);
            }
        }
        return staffResponseDTOS;
    }

    public StaffResponseDTO getStaffById(String staffId) {
        StaffResponseDTO staffResponseDTO = staffServiceClient.getStaffById(staffId);
        if(staffResponseDTO != null) {
            addLinks(staffResponseDTO);
        }
        return staffResponseDTO;
    }

    public StaffResponseDTO createStaff(StaffRequestDTO staffRequestDTO) {
        StaffResponseDTO staffResponseDTO = staffServiceClient.createStaff(staffRequestDTO);
        if(staffResponseDTO != null) {
            addLinks(staffResponseDTO);
            return staffResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + staffRequestDTO);
    }

    public StaffResponseDTO updateStaff(String staffId, StaffRequestDTO staffRequestDTO) {
        StaffResponseDTO staffResponseDTO = staffServiceClient.updateStaff(staffId, staffRequestDTO);
        if(staffResponseDTO != null) {
            addLinks(staffResponseDTO);
            return staffResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + staffRequestDTO);
    }

    public void deleteStaff(String staffId) {
        staffServiceClient.deleteStaff(staffId);
    }

    private StaffResponseDTO addLinks(StaffResponseDTO staffResponseDTO) {
        Link self = linkTo(methodOn(StaffController.class).getStaffById(staffResponseDTO.getStaffId())).withSelfRel();
        Link all = linkTo(methodOn(StaffController.class).getAll()).withRel("All staff");
        return staffResponseDTO.add(all, self);
    }
}
