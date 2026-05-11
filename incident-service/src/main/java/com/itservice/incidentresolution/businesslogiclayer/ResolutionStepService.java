package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.presentationlayer.ResolutionStepRequestDTO;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepResponseDTO;

import java.util.List;

public interface ResolutionStepService {
    List<ResolutionStepResponseDTO> getAllResolutionSteps();
    ResolutionStepResponseDTO getResolutionStepById(String stepId);
    List<ResolutionStepResponseDTO> getResolutionStepsByTicketId(String ticketId);
    ResolutionStepResponseDTO createResolutionStep(String ticketId, ResolutionStepRequestDTO resolutionStepRequestDTO);
    void deleteResolutionStep(String stepId);
}
