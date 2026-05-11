package com.itservice.staffmanagement.staffmanagement.dataaccesslayer;

import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import com.itservice.staffmanagement.staffmanagement.domain.StaffIdentifier;
import com.itservice.staffmanagement.staffmanagement.domain.StaffRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("h2")
class StaffRepositoryIntegrationTest {

    @Autowired
    private StaffRepository staffRepository;

    @Test
    void getStaffByStaffIdentifier_StaffId_withExistingStaff_returnsStaff() {
        Staff staff = Staff.builder()
                .staffIdentifier(new StaffIdentifier("STAFF-9001"))
                .firstName("Sara")
                .lastName("Lee")
                .email("sara.lee@it-support.com")
                .staffRole(StaffRole.AGENT)
                .build();
        staffRepository.save(staff);

        Staff foundStaff = staffRepository.getStaffByStaffIdentifier_StaffId("STAFF-9001");

        assertNotNull(foundStaff);
        assertEquals(StaffRole.AGENT, foundStaff.getStaffRole());
    }

    @Test
    void getStaffByStaffIdentifier_StaffId_withMissingStaff_returnsNull() {
        Staff foundStaff = staffRepository.getStaffByStaffIdentifier_StaffId("STAFF-9999");

        assertNull(foundStaff);
    }
}
