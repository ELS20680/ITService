package com.itservice.staffmanagement.staffmanagement.presentationlayer;


import com.itservice.staffmanagement.staffmanagement.businesslayer.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {
    private StaffService staffService;
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> getAll() {
        List<StaffResponseDTO> response = staffService.getAllStaff();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<StaffResponseDTO> getByStaffId(@PathVariable String staffId) {
        StaffResponseDTO response = staffService.getByStaffId(staffId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StaffResponseDTO> createStaff(@RequestBody StaffRequestDTO staffRequestDTO) {
        StaffResponseDTO staffResponseDTO = staffService.createStaff(staffRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(staffResponseDTO);
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<StaffResponseDTO> updateStaff(@PathVariable String staffId, @RequestBody StaffRequestDTO staffRequestDTO) {
        StaffResponseDTO staffResponseDTO = staffService.updateStaff(staffId, staffRequestDTO);
        return ResponseEntity.ok(staffResponseDTO);
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(@PathVariable String staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}


//@PutMapping("/{assetId}")
//public ResponseEntity<AssetResponseDTO> updateAsset(@PathVariable String assetId, @RequestBody AssetRequestDTO assetRequestDTO) {
//    AssetResponseDTO assetResponseDTO = assetService.updateAsset(assetId, assetRequestDTO);
//    return ResponseEntity.ok(assetResponseDTO);
//}

