package com.itservice.apigatewa.apigateway.domainclientlayer.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerResponseDTO;
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
public class CustomerServiceClient {


    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String CUSTOMER_SERVICE_BASE_URL;

    //constructor - get environment variable values
    public CustomerServiceClient(RestTemplate restTemplate, ObjectMapper mapper,
                                  @Value("${app.customer-service.host}") String customerServiceHost,
                                  @Value("${app.customer-service.port}") String customerServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        CUSTOMER_SERVICE_BASE_URL = "http://" + customerServiceHost + ":" + customerServicePort + "/api/v1/customers";
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        try{
            String url = CUSTOMER_SERVICE_BASE_URL;
            CustomerResponseDTO[] customerResponseDTOS = restTemplate.getForObject(url, CustomerResponseDTO[].class);
            return Arrays.asList(customerResponseDTOS);
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public CustomerResponseDTO getCustomerById(String customerId) {
        try {
            String url = CUSTOMER_SERVICE_BASE_URL + "/" + customerId;
            CustomerResponseDTO customerResponseDTO = restTemplate.getForObject(url, CustomerResponseDTO.class);
            return customerResponseDTO;
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        try{
            String url = CUSTOMER_SERVICE_BASE_URL;
            CustomerResponseDTO customerResponseDTO = restTemplate.postForObject(url, customerRequestDTO, CustomerResponseDTO.class);
            return customerResponseDTO;
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }
    public CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO customerRequestDTO) {
        try {
            String url = CUSTOMER_SERVICE_BASE_URL + "/" + customerId;

            ResponseEntity<CustomerResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(customerRequestDTO),
                    CustomerResponseDTO.class
            );

            return response.getBody();
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    public void deleteCustomer(String customerId) {
        try {
            String url = CUSTOMER_SERVICE_BASE_URL + "/" + customerId;
            restTemplate.delete(url);
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }


    /// ////////////////////EXCEPTIONS////////////////////////

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        }
        catch (IOException ioex) {
            return ioex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {

        //include all possible responses from the client

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


