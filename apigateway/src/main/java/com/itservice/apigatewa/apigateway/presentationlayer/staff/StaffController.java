package com.itservice.apigatewa.apigateway.presentationlayer.staff;

import com.itservice.apigatewa.apigateway.businesslogiclayer.staff.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService){
        this.staffService = staffService;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<StaffResponseDTO>> getAll(){
        return ResponseEntity.ok().body(staffService.getAllStaff());
    }

    @GetMapping(
            value = "/{staffId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StaffResponseDTO> getStaffById(@PathVariable String staffId){
        return ResponseEntity.ok().body(staffService.getStaffById(staffId));
    }

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StaffResponseDTO> createStaff(@RequestBody StaffRequestDTO staffRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(staffRequestDTO));
    }

    @PutMapping(
            value = "/{staffId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StaffResponseDTO> updateStaff(@RequestBody StaffRequestDTO staffRequestDTO, @PathVariable String staffId){
        return ResponseEntity.ok(staffService.updateStaff(staffId, staffRequestDTO));
    }

    @DeleteMapping(
            value = "/{staffId}"
    )
    public ResponseEntity<Void> deleteStaff(@PathVariable String staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}
