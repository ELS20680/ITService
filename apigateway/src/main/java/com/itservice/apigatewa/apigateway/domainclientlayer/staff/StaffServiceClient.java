package com.itservice.apigatewa.apigateway.domainclientlayer.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffResponseDTO;
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
public class StaffServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String STAFF_SERVICE_BASE_URL;

    public StaffServiceClient(RestTemplate restTemplate, ObjectMapper mapper,
                              @Value("${app.staff-service.host}") String staffServiceHost,
                              @Value("${app.staff-service.port}") String staffServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        STAFF_SERVICE_BASE_URL = "http://" + staffServiceHost + ":" + staffServicePort + "/api/v1/staff";
    }

    public List<StaffResponseDTO> getAllStaff() {
        try{
            String url = STAFF_SERVICE_BASE_URL;
            StaffResponseDTO[] staffResponseDTOS = restTemplate.getForObject(url, StaffResponseDTO[].class);
            return Arrays.asList(staffResponseDTOS);
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public StaffResponseDTO getStaffById(String staffId) {
        try {
            String url = STAFF_SERVICE_BASE_URL + "/" + staffId;
            StaffResponseDTO staffResponseDTO = restTemplate.getForObject(url, StaffResponseDTO.class);
            return staffResponseDTO;
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public StaffResponseDTO createStaff(StaffRequestDTO staffRequestDTO) {
        try{
            String url = STAFF_SERVICE_BASE_URL;
            StaffResponseDTO staffResponseDTO = restTemplate.postForObject(url, staffRequestDTO, StaffResponseDTO.class);
            return staffResponseDTO;
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public StaffResponseDTO updateStaff(String staffId, StaffRequestDTO staffRequestDTO) {
        try {
            String url = STAFF_SERVICE_BASE_URL + "/" + staffId;

            ResponseEntity<StaffResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(staffRequestDTO),
                    StaffResponseDTO.class
            );

            return response.getBody();
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    public void deleteStaff(String staffId) {
        try {
            String url = STAFF_SERVICE_BASE_URL + "/" + staffId;
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
