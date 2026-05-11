package com.itservice.incidentresolution.businesslogiclayer;

import com.itservice.incidentresolution.dataccesslayer.ResolutionStepRepository;
import com.itservice.incidentresolution.dataccesslayer.TicketRepository;
import com.itservice.incidentresolution.datamappinglayer.TicketMapper;
import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.domain.TicketIdentifier;
import com.itservice.incidentresolution.domainclientlayer.AssetDomainClient;
import com.itservice.incidentresolution.domainclientlayer.CustomerDomainClient;
import com.itservice.incidentresolution.domainclientlayer.StaffDomainClient;
import com.itservice.incidentresolution.presentationlayer.TicketRequestDTO;
import com.itservice.incidentresolution.presentationlayer.TicketResponseDTO;
import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.TicketNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ResolutionStepRepository resolutionStepRepository;
    private final TicketMapper ticketMapper;
    private final CustomerDomainClient customerDomainClient;
    private final StaffDomainClient staffDomainClient;
    private final AssetDomainClient assetDomainClient;

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponseDTO> getAllTickets() {
        List<Ticket> tickets = this.ticketRepository.findAll();
        List<TicketResponseDTO> responses = new ArrayList<>();

        for (Ticket ticket : tickets) {
            responses.add(buildTicketResponse(ticket));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponseDTO getByTicketId(String ticketId) {
        Ticket ticket = findTicketOrThrow(ticketId);
        return buildTicketResponse(ticket);
    }

    @Override
    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO) {
        CustomerResponseDTO customerResponseDTO = this.customerDomainClient.getCustomerById(ticketRequestDTO.getCustomerId());
        StaffResponseDTO staffResponseDTO = this.staffDomainClient.getStaffById(ticketRequestDTO.getStaffId());
        AssetResponseDTO assetResponseDTO = this.assetDomainClient.getAssetById(ticketRequestDTO.getAssetId());

        Ticket ticket = this.ticketMapper.toEntity(ticketRequestDTO);
        ticket.setTicketIdentifier(new TicketIdentifier());

        Ticket savedTicket = this.ticketRepository.save(ticket);
        return buildTicketResponse(savedTicket, customerResponseDTO, staffResponseDTO, assetResponseDTO);
    }

    @Override
    @Transactional
    public TicketResponseDTO updateTicket(String ticketId, TicketRequestDTO ticketRequestDTO) {
        Ticket foundTicket = findTicketOrThrow(ticketId);

        CustomerResponseDTO customerResponseDTO = this.customerDomainClient.getCustomerById(ticketRequestDTO.getCustomerId());
        StaffResponseDTO staffResponseDTO = this.staffDomainClient.getStaffById(ticketRequestDTO.getStaffId());
        AssetResponseDTO assetResponseDTO = this.assetDomainClient.getAssetById(ticketRequestDTO.getAssetId());

        this.ticketMapper.updateEntityFromDTO(foundTicket, ticketRequestDTO);
        foundTicket.setTicketIdentifier(new TicketIdentifier(ticketId));

        Ticket savedTicket = this.ticketRepository.save(foundTicket);
        return buildTicketResponse(savedTicket, customerResponseDTO, staffResponseDTO, assetResponseDTO);
    }

    @Override
    @Transactional
    public void deleteTicket(String ticketId) {
        Ticket foundTicket = findTicketOrThrow(ticketId);
        this.resolutionStepRepository.deleteByTicketId(ticketId);
        this.ticketRepository.delete(foundTicket);
    }

    private Ticket findTicketOrThrow(String ticketId) {
        Optional<Ticket> ticketOptional = this.ticketRepository.findTicketByTicketIdentifier_TicketId(ticketId);
        if (ticketOptional.isEmpty()) {
            throw new TicketNotFoundException("Ticket with id " + ticketId + " not found");
        }
        return ticketOptional.get();
    }

    private TicketResponseDTO buildTicketResponse(Ticket ticket) {
        CustomerResponseDTO customerResponseDTO = this.customerDomainClient.getCustomerById(ticket.getCustomerId());
        StaffResponseDTO staffResponseDTO = this.staffDomainClient.getStaffById(ticket.getStaffId());
        AssetResponseDTO assetResponseDTO = this.assetDomainClient.getAssetById(ticket.getAssetId());

        return buildTicketResponse(ticket, customerResponseDTO, staffResponseDTO, assetResponseDTO);
    }

    private TicketResponseDTO buildTicketResponse(Ticket ticket,
                                                  CustomerResponseDTO customerResponseDTO,
                                                  StaffResponseDTO staffResponseDTO,
                                                  AssetResponseDTO assetResponseDTO) {
        TicketResponseDTO ticketResponseDTO = this.ticketMapper.toResponseDTO(ticket);
        ticketResponseDTO.setCustomerFirstName(customerResponseDTO.getFirstName());
        ticketResponseDTO.setCustomerLastName(customerResponseDTO.getLastName());
        ticketResponseDTO.setStaffFirstName(staffResponseDTO.getFirstName());
        ticketResponseDTO.setStaffLastName(staffResponseDTO.getLastName());
        ticketResponseDTO.setAssetType(assetResponseDTO.getType());
        return ticketResponseDTO;
    }
}
