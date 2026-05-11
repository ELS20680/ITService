package com.itservice.incidentresolution.datamappinglayer;

import com.itservice.incidentresolution.domain.Ticket;
import com.itservice.incidentresolution.presentationlayer.TicketRequestDTO;
import com.itservice.incidentresolution.presentationlayer.TicketResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticketIdentifier", ignore = true)
    Ticket toEntity(TicketRequestDTO ticketRequestDTO);

    @Mapping(target = "ticketId", expression = "java(ticket.getTicketIdentifier().getTicketId())")
    @Mapping(target = "customerFirstName", ignore = true)
    @Mapping(target = "customerLastName", ignore = true)
    @Mapping(target = "staffFirstName", ignore = true)
    @Mapping(target = "staffLastName", ignore = true)
    @Mapping(target = "assetType", ignore = true)
    TicketResponseDTO toResponseDTO(Ticket ticket);

    List<TicketResponseDTO> toResponseDTOList(List<Ticket> tickets);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ticketIdentifier", ignore = true)
    void updateEntityFromDTO(@MappingTarget Ticket ticket, TicketRequestDTO ticketRequestDTO);
}
