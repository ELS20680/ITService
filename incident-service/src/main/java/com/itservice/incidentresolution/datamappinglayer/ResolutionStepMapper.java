package com.itservice.incidentresolution.datamappinglayer;

import com.itservice.incidentresolution.domain.ResolutionStep;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepRequestDTO;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ResolutionStepMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stepIdentifier", ignore = true)
    @Mapping(target = "ticketId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ResolutionStep toEntity(ResolutionStepRequestDTO resolutionStepRequestDTO);

    @Mapping(target = "stepId", expression = "java(resolutionStep.getStepIdentifier().getStepId())")
    ResolutionStepResponseDTO toResponseDTO(ResolutionStep resolutionStep);

    List<ResolutionStepResponseDTO> toResponseDTOList(List<ResolutionStep> resolutionSteps);
}
