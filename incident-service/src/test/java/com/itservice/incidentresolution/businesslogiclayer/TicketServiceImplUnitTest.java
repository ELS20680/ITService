package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.dataccesslayer.ResolutionStepRepository;
import com.itservice.incidentresolution.dataccesslayer.TicketRepository;
import com.itservice.incidentresolution.datamappinglayer.TicketMapperImpl;
import com.itservice.incidentresolution.domain.Priority;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketIdentifier;
import com.itservice.incidentresolution.domain.TicketStatus;
import com.itservice.incidentresolution.domainclientlayer.AssetDomainClient;
import com.itservice.incidentresolution.domainclientlayer.CustomerDomainClient;
import com.itservice.incidentresolution.domainclientlayer.StaffDomainClient;
import com.itservice.incidentresolution.presentationlayer.TicketRequestDTO;
import com.itservice.incidentresolution.presentationlayer.TicketResponseDTO;
import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.CustomerNotFoundException;
import com.itservice.incidentresolution.utilities.TicketNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplUnitTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ResolutionStepRepository resolutionStepRepository;

    @Mock
    private CustomerDomainClient customerDomainClient;

    @Mock
    private StaffDomainClient staffDomainClient;

    @Mock
    private AssetDomainClient assetDomainClient;

    private TicketServiceImpl ticketService;

    @BeforeEach
    void setup() {
        ticketService = new TicketServiceImpl(
                ticketRepository,
                resolutionStepRepository,
                new TicketMapperImpl(),
                customerDomainClient,
                staffDomainClient,
                assetDomainClient
        );
    }

    @Test
    void getAllTickets_returnsTicketsWithClientData() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket("TIC-1001")));
        mockClientResponses();

        List<TicketResponseDTO> tickets = ticketService.getAllTickets();

        assertEquals(1, tickets.size());
        assertEquals("Alice", tickets.get(0).getCustomerFirstName());
    }

    @Test
    void getByTicketId_withMissingTicket_throwsTicketNotFoundException() {
        when(ticketRepository.findTicketByTicketIdentifier_TicketId("bad-id")).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.getByTicketId("bad-id"));
    }

    @Test
    void createTicket_withValidReferences_savesTicket() {
        mockClientResponses();
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setId("mongo-id-1");
            return ticket;
        });

        TicketResponseDTO response = ticketService.createTicket(request());

        assertEquals("Alice", response.getCustomerFirstName());
        assertEquals("LAPTOP", response.getAssetType());
    }

    @Test
    void createTicket_whenCustomerMissing_throwsCustomerNotFoundException() {
        when(customerDomainClient.getCustomerById("CUST-001"))
                .thenThrow(new CustomerNotFoundException("Customer with id CUST-001 not found"));

        assertThrows(CustomerNotFoundException.class, () -> ticketService.createTicket(request()));
    }

    @Test
    void updateTicket_withExistingTicket_keepsTicketId() {
        when(ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-1001"))
                .thenReturn(Optional.of(ticket("TIC-1001")));
        mockClientResponses();
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponseDTO response = ticketService.updateTicket("TIC-1001", request());

        assertEquals("TIC-1001", response.getTicketId());
        assertEquals("David", response.getStaffFirstName());
    }

    @Test
    void deleteTicket_withExistingTicket_deletesStepsAndTicket() {
        Ticket ticket = ticket("TIC-1001");
        when(ticketRepository.findTicketByTicketIdentifier_TicketId("TIC-1001"))
                .thenReturn(Optional.of(ticket));

        ticketService.deleteTicket("TIC-1001");

        verify(resolutionStepRepository).deleteByTicketId("TIC-1001");
        verify(ticketRepository).delete(ticket);
    }

    private void mockClientResponses() {
        when(customerDomainClient.getCustomerById("CUST-001")).thenReturn(
                CustomerResponseDTO.builder()
                        .customerId("CUST-001")
                        .firstName("Alice")
                        .lastName("Johnson")
                        .build()
        );
        when(staffDomainClient.getStaffById("STAFF-101")).thenReturn(
                StaffResponseDTO.builder()
                        .staffId("STAFF-101")
                        .firstName("David")
                        .lastName("Miller")
                        .build()
        );
        when(assetDomainClient.getAssetById("AST-5001")).thenReturn(
                AssetResponseDTO.builder()
                        .assetId("AST-5001")
                        .type("LAPTOP")
                        .status("IN_SERVICE")
                        .build()
        );
    }

    private TicketRequestDTO request() {
        return TicketRequestDTO.builder()
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }

    private Ticket ticket(String ticketId) {
        return Ticket.builder()
                .id("mongo-id-1")
                .ticketIdentifier(new TicketIdentifier(ticketId))
                .title("Laptop will not boot")
                .description("Customer laptop does not power on after update.")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }
}
