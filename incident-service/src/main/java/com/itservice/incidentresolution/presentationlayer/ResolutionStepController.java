package com.itservice.incidentresolution.presentationlayer;

import com.itservice.incidentresolution.businesslogiclayer.ResolutionStepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResolutionStepController {

    private final ResolutionStepService resolutionStepService;

    @GetMapping("/api/v1/resolution-step")
    public ResponseEntity<List<ResolutionStepResponseDTO>> getAllResolutionSteps() {
        return ResponseEntity.ok(this.resolutionStepService.getAllResolutionSteps());
    }

    @GetMapping("/api/v1/resolution-step/{stepId}")
    public ResponseEntity<ResolutionStepResponseDTO> getResolutionStepById(@PathVariable String stepId) {
        return ResponseEntity.ok(this.resolutionStepService.getResolutionStepById(stepId));
    }

    @GetMapping("/api/v1/tickets/{ticketId}/resolution-step")
    public ResponseEntity<List<ResolutionStepResponseDTO>> getResolutionStepsByTicketId(@PathVariable String ticketId) {
        return ResponseEntity.ok(this.resolutionStepService.getResolutionStepsByTicketId(ticketId));
    }

    @PostMapping("/api/v1/tickets/{ticketId}/resolution-step")
    public ResponseEntity<ResolutionStepResponseDTO> createResolutionStep(@PathVariable String ticketId,
                                                                          @Valid @RequestBody ResolutionStepRequestDTO resolutionStepRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.resolutionStepService.createResolutionStep(ticketId, resolutionStepRequestDTO));
    }

    @DeleteMapping("/api/v1/resolution-step/{stepId}")
    public ResponseEntity<Void> deleteResolutionStep(@PathVariable String stepId) {
        this.resolutionStepService.deleteResolutionStep(stepId);
        return ResponseEntity.noContent().build();
    }
}
