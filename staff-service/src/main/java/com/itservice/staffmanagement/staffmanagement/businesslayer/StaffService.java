package com.itservice.staffmanagement.staffmanagement.businesslayer;

;
import com.itservice.staffmanagement.staffmanagement.dataaccesslayer.StaffRepository;
import com.itservice.staffmanagement.staffmanagement.datamappinglayer.StaffMapper;

import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import com.itservice.staffmanagement.staffmanagement.domain.StaffIdentifier;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffRequestDTO;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffResponseDTO;

import com.itservice.staffmanagement.staffmanagement.utilities.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StaffService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(staffMapper::toDTO)
                .collect(Collectors.toList());
    }

    public StaffResponseDTO getByStaffId(String staffId) {
        Staff staff = staffRepository.getStaffByStaffIdentifier_StaffId(staffId);
        if (staff == null) {
            throw new NotFoundException("Staff not found "+staffId);
        }
        return staffMapper.toDTO(staff);
    }

    public StaffResponseDTO createStaff(StaffRequestDTO staffRequestDTO) {
        StaffIdentifier staffIdentifier = new StaffIdentifier();
        Staff staff = staffMapper.toEntity(staffRequestDTO);
        staff.setStaffIdentifier(staffIdentifier);
        Staff persistedStaff = staffRepository.save(staff);
        return staffMapper.toDTO(persistedStaff);
    }

    public StaffResponseDTO updateStaff(String staffId, StaffRequestDTO staffRequestDTO)
    {
        Staff staff = staffRepository.getStaffByStaffIdentifier_StaffId(staffId);
        if (staff == null) {
            throw new NotFoundException("Staff not found "+staffId);
        }
        Staff requestedStaff = staffMapper.toEntity(staffRequestDTO);
        requestedStaff.setStaffIdentifier(staff.getStaffIdentifier());
        requestedStaff.setId(staff.getId());
        Staff persistedStaff = staffRepository.save(requestedStaff);
        return staffMapper.toDTO(persistedStaff);
    }

    public void deleteStaff(String staffId) {
        if(staffId == null) {
            throw new NotFoundException("Staff id is null: " + staffId);
        }

        staffRepository.deleteById(staffId);
    }
}



