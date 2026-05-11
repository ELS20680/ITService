package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.dataccesslayer.ResolutionStepRepository;
import com.itservice.incidentresolution.dataccesslayer.TicketRepository;
import com.itservice.incidentresolution.datamappinglayer.ResolutionStepMapper;
import com.itservice.incidentresolution.domain.ResolutionStep;
import com.itservice.incidentresolution.domain.StepIdentifier;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketStatus;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepRequestDTO;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepResponseDTO;
import com.itservice.incidentresolution.utilities.ResolutionStepNotFoundException;
import com.itservice.incidentresolution.utilities.TicketAlreadyClosedException;
import com.itservice.incidentresolution.utilities.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResolutionStepServiceImpl implements ResolutionStepService {

    private final ResolutionStepRepository resolutionStepRepository;
    private final TicketRepository ticketRepository;
    private final ResolutionStepMapper resolutionStepMapper;

    @Override
    public List<ResolutionStepResponseDTO> getAllResolutionSteps() {
        return this.resolutionStepMapper.toResponseDTOList(this.resolutionStepRepository.findAll());
    }

    @Override
    public ResolutionStepResponseDTO getResolutionStepById(String stepId) {
        ResolutionStep resolutionStep = findStepOrThrow(stepId);
        return this.resolutionStepMapper.toResponseDTO(resolutionStep);
    }

    @Override
    public List<ResolutionStepResponseDTO> getResolutionStepsByTicketId(String ticketId) {
        if (!this.ticketRepository.existsByTicketIdentifier_TicketId(ticketId)) {
            throw new TicketNotFoundException("Ticket with id " + ticketId + " not found");
        }

        return this.resolutionStepMapper.toResponseDTOList(
                this.resolutionStepRepository.findByTicketIdOrderByStepNumberAsc(ticketId)
        );
    }

    @Override
    public ResolutionStepResponseDTO createResolutionStep(String ticketId, ResolutionStepRequestDTO resolutionStepRequestDTO) {
        Optional<Ticket> ticketOptional = this.ticketRepository.findTicketByTicketIdentifier_TicketId(ticketId);
        if (ticketOptional.isEmpty()) {
            throw new TicketNotFoundException("Ticket with id " + ticketId + " not found");
        }
        if (ticketOptional.get().getStatus() == TicketStatus.CLOSED) {
            throw new TicketAlreadyClosedException("Ticket with id " + ticketId + " is already closed");
        }

        ResolutionStep resolutionStep = this.resolutionStepMapper.toEntity(resolutionStepRequestDTO);
        resolutionStep.setStepIdentifier(new StepIdentifier());
        resolutionStep.setTicketId(ticketId);
        resolutionStep.setCreatedAt(LocalDateTime.now());

        ResolutionStep savedResolutionStep = this.resolutionStepRepository.save(resolutionStep);
        return this.resolutionStepMapper.toResponseDTO(savedResolutionStep);
    }

    @Override
    public void deleteResolutionStep(String stepId) {
        ResolutionStep resolutionStep = findStepOrThrow(stepId);
        this.resolutionStepRepository.delete(resolutionStep);
    }

    private ResolutionStep findStepOrThrow(String stepId) {
        Optional<ResolutionStep> resolutionStepOptional = this.resolutionStepRepository
                .findResolutionStepByStepIdentifier_StepId(stepId);

        if (resolutionStepOptional.isEmpty()) {
            throw new ResolutionStepNotFoundException("Resolution step with id " + stepId + " not found");
        }

        return resolutionStepOptional.get();
    }
}
