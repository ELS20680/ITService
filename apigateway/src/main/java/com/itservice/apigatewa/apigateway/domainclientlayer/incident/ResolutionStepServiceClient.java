package com.itservice.apigatewa.apigateway.domainclientlayer.incident;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.HttpErrorInfo;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
public class ResolutionStepServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String INCIDENT_SERVICE_BASE_URL;

    public ResolutionStepServiceClient(RestTemplate restTemplate, ObjectMapper mapper,
                                       @Value("${app.incident-service.host}") String incidentServiceHost,
                                       @Value("${app.incident-service.port}") String incidentServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        INCIDENT_SERVICE_BASE_URL = "http://" + incidentServiceHost + ":" + incidentServicePort;
    }

    public List<ResolutionStepResponseDTO> getAllResolutionSteps() {
        try{
            String url = INCIDENT_SERVICE_BASE_URL + "/api/v1/resolution-step";
            ResolutionStepResponseDTO[] resolutionStepResponseDTOS = restTemplate.getForObject(url, ResolutionStepResponseDTO[].class);
            return Arrays.asList(resolutionStepResponseDTOS);
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public ResolutionStepResponseDTO getResolutionStepById(String stepId) {
        try {
            String url = INCIDENT_SERVICE_BASE_URL + "/api/v1/resolution-step/" + stepId;
            ResolutionStepResponseDTO resolutionStepResponseDTO = restTemplate.getForObject(url, ResolutionStepResponseDTO.class);
            return resolutionStepResponseDTO;
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public List<ResolutionStepResponseDTO> getResolutionStepsByTicketId(String ticketId) {
        try {
            String url = INCIDENT_SERVICE_BASE_URL + "/api/v1/tickets/" + ticketId + "/resolution-step";
            ResolutionStepResponseDTO[] resolutionStepResponseDTOS = restTemplate.getForObject(url, ResolutionStepResponseDTO[].class);
            return Arrays.asList(resolutionStepResponseDTOS);
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public ResolutionStepResponseDTO createResolutionStep(String ticketId, ResolutionStepRequestDTO resolutionStepRequestDTO) {
        try{
            String url = INCIDENT_SERVICE_BASE_URL + "/api/v1/tickets/" + ticketId + "/resolution-step";
            ResolutionStepResponseDTO resolutionStepResponseDTO = restTemplate.postForObject(url, resolutionStepRequestDTO, ResolutionStepResponseDTO.class);
            return resolutionStepResponseDTO;
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public void deleteResolutionStep(String stepId) {
        try {
            String url = INCIDENT_SERVICE_BASE_URL + "/api/v1/resolution-step/" + stepId;
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
