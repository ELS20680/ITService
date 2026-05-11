package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.dataccesslayer.ResolutionStepRepository;
import com.itservice.incidentresolution.dataccesslayer.TicketRepository;
import com.itservice.incidentresolution.datamappinglayer.ResolutionStepMapperImpl;
import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.ResolutionStep;
import com.itservice.incidentresolution.domain.StepIdentifier;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketIdentifier;
import com.itservice.incidentresolution.domain.TicketStatus;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepRequestDTO;
import com.itservice.incidentresolution.presentationlayer.ResolutionStepResponseDTO;
import com.itservice.incidentresolution.utilities.ResolutionStepNotFoundException;
import com.itservice.incidentresolution.utilities.TicketAlreadyClosedException;
import com.itservice.incidentresolution.utilities.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolutionStepServiceImplUnitTest {

    @Mock
    private ResolutionStepRepository resolutionStepRepository;

    @Mock
    private TicketRepository ticketRepository;

    private ResolutionStepServiceImpl resolutionStepService;

    @BeforeEach
    void setup() {
        resolutionStepService = new ResolutionStepServiceImpl(
                resolutionStepRepository,
                ticketRepository,
                new ResolutionStepMapperImpl()
        );
    }

    @Test
    void getAllResolutionSteps_returnsSteps() {
        when(resolutionStepRepository.findAll()).thenReturn(List.of(step("STEP-1001", "TIC-1001", 1)));

        List<ResolutionStepResponseDTO> steps = resolutionStepService.getAllResolutionSteps();

        assertEquals(1, steps.size());
        assertEquals("STEP-1001", steps.get(0).getStepId());
    }

    @Test
    void getResolutionStepById_withMissingStep_throwsResolutionStepNotFoundException() {
        when(resolutionStepRepository.findResolutionStepByStepIdentifier_StepId("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResolutionStepNotFoundException.class,
                () -> resolutionStepService.getResolutionStepById("bad-id"));
    }

    @Test
    void getResolutionStepsByTicketId_withMissingTicket_throwsTicketNotFoundException() {
        when(ticketRepository.existsByTicketIdentifier_TicketId("bad-id")).thenReturn(false);

        assertThrows(TicketNotFoundException.class,
                () -> resolutionStepService.getResolutionStepsByTicketId("bad-id"));
    }

    @Test
    void createResolutionStep_withOpenTicket_savesStep() {
        when(ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-1001"))
                .thenReturn(Optional.of(ticket("TIC-1001", TicketStatus.IN_PROGRESS)));
        when(resolutionStepRepository.save(any(ResolutionStep.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResolutionStepResponseDTO response = resolutionStepService.createResolutionStep("TIC-1001", request());

        assertNotNull(response.getStepId());
        assertEquals("TIC-1001", response.getTicketId());
    }

    @Test
    void createResolutionStep_withClosedTicket_throwsTicketAlreadyClosedException() {
        when(ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-1001"))
                .thenReturn(Optional.of(ticket("TIC-1001", TicketStatus.CLOSED)));

        assertThrows(TicketAlreadyClosedException.class,
                () -> resolutionStepService.createResolutionStep("TIC-1001", request()));
    }

    @Test
    void deleteResolutionStep_withExistingStep_deletesStep() {
        ResolutionStep step = step("STEP-1001", "TIC-1001", 1);
        when(resolutionStepRepository.findResolutionStepByStepIdentifier_StepId("STEP-1001"))
                .thenReturn(Optional.of(step));

        resolutionStepService.deleteResolutionStep("STEP-1001");

        verify(resolutionStepRepository).delete(step);
    }

    private ResolutionStepRequestDTO request() {
        return ResolutionStepRequestDTO.builder()
                .stepNumber(1)
                .actionTaken("Checked the device")
                .result("Device needs repair")
                .build();
    }

    private ResolutionStep step(String stepId, String ticketId, Integer stepNumber) {
        return ResolutionStep.builder()
                .stepIdentifier(new StepIdentifier(stepId))
                .ticketId(ticketId)
                .stepNumber(stepNumber)
                .actionTaken("Checked the device")
                .result("Device needs repair")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Ticket ticket(String ticketId, TicketStatus status) {
        return Ticket.builder()
                .ticketIdentifier(new TicketIdentifier(ticketId))
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(status)
                .priority(Priority.HIGH)
                .build();
    }
}
