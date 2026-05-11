package com.itservice.incidentresolution.dataccesslayer;

import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.ResolutionStep;
import com.itservice.incidentresolution.domain.StepIdentifier;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketIdentifier;
import com.itservice.incidentresolution.domain.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ActiveProfiles("testing-profile")
class IncidentRepositoryIntegrationTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ResolutionStepRepository resolutionStepRepository;

    @Test
    void findTicketByTicketIdentifier_TicketId_withExistingTicket_returnsTicket() {
        ticketRepository.save(ticket("TIC-9001", TicketStatus.NEW));

        Optional<Ticket> foundTicket = ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-9001");

        assertTrue(foundTicket.isPresent());
        assertEquals(Priority.HIGH, foundTicket.get().getPriority());
    }

    @Test
    void findTicketByTicketIdentifier_TicketId_withMissingTicket_returnsEmpty() {
        Optional<Ticket> foundTicket = ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-9999");

        assertFalse(foundTicket.isPresent());
    }

    @Test
    void findByTicketIdOrderByStepNumberAsc_withSteps_returnsOrderedSteps() {
        resolutionStepRepository.save(step("STEP-2", "TIC-9001", 2));
        resolutionStepRepository.save(step("STEP-1", "TIC-9001", 1));

        List<ResolutionStep> steps = resolutionStepRepository.findByTicketIdOrderByStepNumberAsc("TIC-9001");

        assertEquals(2, steps.size());
        assertEquals(1, steps.get(0).getStepNumber());
    }

    @Test
    void findResolutionStepByStepIdentifier_StepId_withMissingStep_returnsEmpty() {
        Optional<ResolutionStep> foundStep = resolutionStepRepository.findResolutionStepByStepIdentifier_StepId("STEP-9999");

        assertFalse(foundStep.isPresent());
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

    private ResolutionStep step(String stepId, String ticketId, Integer stepNumber) {
        return ResolutionStep.builder()
                .stepIdentifier(new StepIdentifier(stepId))
                .ticketId(ticketId)
                .stepNumber(stepNumber)
                .actionTaken("Checked the device")
                .result("Step complete")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
