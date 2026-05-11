package com.itservice.apigatewa.apigateway.domainclientlayer.incident;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.HttpErrorInfo;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class TicketServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String TICKET_SERVICE_BASE_URL;

    public TicketServiceClient(RestTemplate restTemplate, ObjectMapper mapper,
                               @Value("${app.incident-service.host}") String incidentServiceHost,
                               @Value("${app.incident-service.port}") String incidentServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        TICKET_SERVICE_BASE_URL = "http://" + incidentServiceHost + ":" + incidentServicePort + "/api/v1/tickets";
    }

    public List<TicketResponseDTO> getAllTickets() {
        try{
            String url = TICKET_SERVICE_BASE_URL;
            TicketResponseDTO[] ticketResponseDTOS = restTemplate.getForObject(url, TicketResponseDTO[].class);
            return Arrays.asList(ticketResponseDTOS);
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public TicketResponseDTO getByTicketId(String ticketId) {
        try {
            String url = TICKET_SERVICE_BASE_URL + "/" + ticketId;
            TicketResponseDTO ticketResponseDTO = restTemplate.getForObject(url, TicketResponseDTO.class);
            return ticketResponseDTO;
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public TicketResponseDTO createTicket(TicketRequestDTO ticketRequestDTO) {
        try{
            String url = TICKET_SERVICE_BASE_URL;
            TicketResponseDTO ticketResponseDTO = restTemplate.postForObject(url, ticketRequestDTO, TicketResponseDTO.class);
            return ticketResponseDTO;
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public TicketResponseDTO updateTicket(String ticketId, TicketRequestDTO ticketRequestDTO) {
        try {
            String url = TICKET_SERVICE_BASE_URL + "/" + ticketId;

            ResponseEntity<TicketResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(ticketRequestDTO),
                    TicketResponseDTO.class
            );

            return response.getBody();
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    public void deleteTicket(String ticketId) {
        try {
            String url = TICKET_SERVICE_BASE_URL + "/" + ticketId;
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {

        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }

        log.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return new RuntimeException(getErrorMessage(ex), ex);
    }
}
