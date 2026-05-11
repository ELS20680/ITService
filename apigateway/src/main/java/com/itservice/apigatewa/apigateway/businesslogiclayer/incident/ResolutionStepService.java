package com.itservice.apigatewa.apigateway.businesslogiclayer.incident;

import com.itservice.apigatewa.apigateway.domainclientlayer.incident.ResolutionStepServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepController;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ResolutionStepService {
    private final ResolutionStepServiceClient resolutionStepServiceClient;

    public ResolutionStepService(ResolutionStepServiceClient resolutionStepServiceClient) {
        this.resolutionStepServiceClient = resolutionStepServiceClient;
    }

    public List<ResolutionStepResponseDTO> getAllResolutionSteps() {
        List<ResolutionStepResponseDTO> resolutionStepResponseDTOS = resolutionStepServiceClient.getAllResolutionSteps();
        if(resolutionStepResponseDTOS != null) {
            for (ResolutionStepResponseDTO resolutionStepResponseDTO : resolutionStepResponseDTOS) {
                addLinks(resolutionStepResponseDTO);
            }
        }
        return resolutionStepResponseDTOS;
    }

    public ResolutionStepResponseDTO getResolutionStepById(String stepId) {
        ResolutionStepResponseDTO resolutionStepResponseDTO = resolutionStepServiceClient.getResolutionStepById(stepId);
        if(resolutionStepResponseDTO != null) {
            addLinks(resolutionStepResponseDTO);
        }
        return resolutionStepResponseDTO;
    }

    public List<ResolutionStepResponseDTO> getResolutionStepsByTicketId(String ticketId) {
        List<ResolutionStepResponseDTO> resolutionStepResponseDTOS = resolutionStepServiceClient.getResolutionStepsByTicketId(ticketId);
        if(resolutionStepResponseDTOS != null) {
            for (ResolutionStepResponseDTO resolutionStepResponseDTO : resolutionStepResponseDTOS) {
                addLinks(resolutionStepResponseDTO);
            }
        }
        return resolutionStepResponseDTOS;
    }

    public ResolutionStepResponseDTO createResolutionStep(String ticketId, ResolutionStepRequestDTO resolutionStepRequestDTO) {
        ResolutionStepResponseDTO resolutionStepResponseDTO = resolutionStepServiceClient.createResolutionStep(ticketId, resolutionStepRequestDTO);
        if(resolutionStepResponseDTO != null) {
            addLinks(resolutionStepResponseDTO);
            return resolutionStepResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + resolutionStepRequestDTO);
    }

    public void deleteResolutionStep(String stepId) {
        resolutionStepServiceClient.deleteResolutionStep(stepId);
    }

    private ResolutionStepResponseDTO addLinks(ResolutionStepResponseDTO resolutionStepResponseDTO) {
        Link self = linkTo(methodOn(ResolutionStepController.class).getResolutionStepById(resolutionStepResponseDTO.getStepId())).withSelfRel();
        Link all = linkTo(methodOn(ResolutionStepController.class).getAllResolutionSteps()).withRel("All resolution steps");
        return resolutionStepResponseDTO.add(all, self);
    }
}
