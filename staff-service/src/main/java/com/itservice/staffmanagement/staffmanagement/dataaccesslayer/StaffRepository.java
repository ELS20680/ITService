package com.itservice.staffmanagement.staffmanagement.dataaccesslayer;


import com.itservice.staffmanagement.staffmanagement.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
Staff findByStaffIdentifier_StaffId(String staffId);

    Staff getStaffByStaffIdentifier_StaffId(String staffIdentifierStaffId);
}

