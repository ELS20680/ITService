package com.itservice.apigatewa.apigateway.domainclientlayer.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
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
public class AssetServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String ASSET_SERVICE_BASE_URL;

    public AssetServiceClient(RestTemplate restTemplate, ObjectMapper mapper,
                              @Value("${app.asset-service.host}") String assetServiceHost,
                              @Value("${app.asset-service.port}") String assetServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        ASSET_SERVICE_BASE_URL = "http://" + assetServiceHost + ":" + assetServicePort + "/api/v1/assets";
    }

    public List<AssetResponseDTO> getAllAssets() {
        try{
            String url = ASSET_SERVICE_BASE_URL;
            AssetResponseDTO[] assetResponseDTOS = restTemplate.getForObject(url, AssetResponseDTO[].class);
            return Arrays.asList(assetResponseDTOS);
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public AssetResponseDTO getAssetById(String assetId) {
        try {
            String url = ASSET_SERVICE_BASE_URL + "/" + assetId;
            AssetResponseDTO assetResponseDTO = restTemplate.getForObject(url, AssetResponseDTO.class);
            return assetResponseDTO;
        }
        catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public AssetResponseDTO createAsset(AssetRequestDTO assetRequestDTO) {
        try{
            String url = ASSET_SERVICE_BASE_URL;
            AssetResponseDTO assetResponseDTO = restTemplate.postForObject(url, assetRequestDTO, AssetResponseDTO.class);
            return assetResponseDTO;
        }catch(HttpClientErrorException e){
            throw handleHttpClientException(e);
        }
    }

    public AssetResponseDTO updateAsset(String assetId, AssetRequestDTO assetRequestDTO) {
        try {
            String url = ASSET_SERVICE_BASE_URL + "/" + assetId;

            ResponseEntity<AssetResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    new HttpEntity<>(assetRequestDTO),
                    AssetResponseDTO.class
            );

            return response.getBody();
        }
        catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    public void deleteAsset(String assetId) {
        try {
            String url = ASSET_SERVICE_BASE_URL + "/" + assetId;
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
