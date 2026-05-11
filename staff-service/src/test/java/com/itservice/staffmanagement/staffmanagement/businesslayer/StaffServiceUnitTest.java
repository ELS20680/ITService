package com.itservice.staffmanagement.staffmanagement.businesslayer;

import com.itservice.staffmanagement.staffmanagement.dataaccesslayer.StaffRepository;
import com.itservice.staffmanagement.staffmanagement.datamappinglayer.StaffMapper;
import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import com.itservice.staffmanagement.staffmanagement.domain.StaffIdentifier;
import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffRequestDTO;
import com.itservice.staffmanagement.staffmanagement.presentationlayer.StaffResponseDTO;
import com.itservice.staffmanagement.staffmanagement.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffServiceUnitTest {

    @Mock
    private StaffRepository staffRepository;

    private StaffService staffService;

    @BeforeEach
    void setup() {
        staffService = new StaffService(staffRepository, new StaffMapper());
    }

    @Test
    void getAllStaff_returnsMappedStaff() {
        when(staffRepository.findAll()).thenReturn(List.of(staff("STAFF-1001")));

        List<StaffResponseDTO> staff = staffService.getAllStaff();

        assertEquals(1, staff.size());
        assertEquals("STAFF-1001", staff.get(0).getStaffId());
    }

    @Test
    void getByStaffId_withExistingStaff_returnsStaff() {
        when(staffRepository.getStaffByStaffIdentifier_StaffId("STAFF-1001")).thenReturn(staff("STAFF-1001"));

        StaffResponseDTO staff = staffService.getByStaffId("STAFF-1001");

        assertEquals(StaffRole.AGENT, staff.getStaffRole());
    }

    @Test
    void getByStaffId_withMissingStaff_throwsNotFoundException() {
        when(staffRepository.getStaffByStaffIdentifier_StaffId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> staffService.getByStaffId("bad-id"));
    }

    @Test
    void createStaff_savesStaffAndReturnsResponse() {
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff staff = invocation.getArgument(0);
            staff.setId(1L);
            return staff;
        });

        StaffResponseDTO response = staffService.createStaff(request());

        assertNotNull(response.getStaffId());
        assertEquals(StaffRole.AGENT, response.getStaffRole());
    }

    @Test
    void updateStaff_withExistingStaff_keepsStaffId() {
        when(staffRepository.getStaffByStaffIdentifier_StaffId("STAFF-1001")).thenReturn(staff("STAFF-1001"));
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StaffResponseDTO response = staffService.updateStaff("STAFF-1001", request());

        assertEquals("STAFF-1001", response.getStaffId());
    }

    @Test
    void deleteStaff_withMissingStaff_throwsNotFoundException() {
        when(staffRepository.getStaffByStaffIdentifier_StaffId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> staffService.deleteStaff("bad-id"));
    }

    @Test
    void deleteStaff_withNullStaffId_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> staffService.deleteStaff(null));
    }

    @Test
    void deleteStaff_withExistingStaff_deletesStaff() {
        Staff staff = staff("STAFF-1001");
        when(staffRepository.getStaffByStaffIdentifier_StaffId("STAFF-1001")).thenReturn(staff);

        staffService.deleteStaff("STAFF-1001");

        verify(staffRepository).delete(staff);
    }

    private Staff staff(String staffId) {
        return Staff.builder()
                .id(1L)
                .staffIdentifier(new StaffIdentifier(staffId))
                .firstName("David")
                .lastName("Miller")
                .email("d.miller@it-support.com")
                .staffRole(StaffRole.AGENT)
                .build();
    }

    private StaffRequestDTO request() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("David");
        requestDTO.setLastName("Miller");
        requestDTO.setEmail("d.miller@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);
        return requestDTO;
    }
}
