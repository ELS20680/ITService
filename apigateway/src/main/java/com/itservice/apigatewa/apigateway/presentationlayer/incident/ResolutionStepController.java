package com.itservice.apigatewa.apigateway.presentationlayer.incident;

import com.itservice.apigatewa.apigateway.businesslogiclayer.incident.ResolutionStepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ResolutionStepController {
    private final ResolutionStepService resolutionStepService;

    public ResolutionStepController(ResolutionStepService resolutionStepService) {
        this.resolutionStepService = resolutionStepService;
    }

    @GetMapping(
            value = "/resolution-step",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ResolutionStepResponseDTO>> getAllResolutionSteps() {
        return ResponseEntity.ok().body(resolutionStepService.getAllResolutionSteps());
    }

    @GetMapping(
            value = "/resolution-step/{stepId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResolutionStepResponseDTO> getResolutionStepById(@PathVariable String stepId) {
        return ResponseEntity.ok().body(resolutionStepService.getResolutionStepById(stepId));
    }

    @GetMapping(
            value = "/tickets/{ticketId}/resolution-step",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ResolutionStepResponseDTO>> getResolutionStepsByTicketId(@PathVariable String ticketId) {
        return ResponseEntity.ok().body(resolutionStepService.getResolutionStepsByTicketId(ticketId));
    }

    @PostMapping(
            value = "/tickets/{ticketId}/resolution-step",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResolutionStepResponseDTO> createResolutionStep(@RequestBody ResolutionStepRequestDTO resolutionStepRequestDTO,
                                                                          @PathVariable String ticketId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resolutionStepService.createResolutionStep(ticketId, resolutionStepRequestDTO));
    }

    @DeleteMapping(
            value = "/resolution-step/{stepId}"
    )
    public ResponseEntity<Void> deleteResolutionStep(@PathVariable String stepId) {
        resolutionStepService.deleteResolutionStep(stepId);
        return ResponseEntity.noContent().build();
    }
}
