package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.AssetNotFoundException;
import com.itservice.incidentresolution.utilities.CustomerNotFoundException;
import com.itservice.incidentresolution.utilities.StaffNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DomainClientUnitTest {

    @Test
    void getCustomerById_withExistingCustomer_returnsCustomer() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        CustomerDomainClient client = new CustomerDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "customerServiceBaseUrl", "http://customer-service");

        mockWebClient(webClientBuilder, "http://customer-service", "/api/v1/customers/{id}", "CUST-001",
                CustomerResponseDTO.class,
                CustomerResponseDTO.builder().customerId("CUST-001").firstName("Alice").build());

        CustomerResponseDTO response = client.getCustomerById("CUST-001");

        assertEquals("Alice", response.getFirstName());
    }

    @Test
    void getCustomerById_withMissingCustomer_throwsCustomerNotFoundException() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        CustomerDomainClient client = new CustomerDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "customerServiceBaseUrl", "http://customer-service");

        mockWebClientNotFound(webClientBuilder, "http://customer-service", "/api/v1/customers/{id}", "CUST-404", CustomerResponseDTO.class);

        assertThrows(CustomerNotFoundException.class, () -> client.getCustomerById("CUST-404"));
    }

    @Test
    void getStaffById_withExistingStaff_returnsStaff() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        StaffDomainClient client = new StaffDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "staffServiceBaseUrl", "http://staff-service");

        mockWebClient(webClientBuilder, "http://staff-service", "/api/v1/staff/{id}", "STAFF-101",
                StaffResponseDTO.class,
                StaffResponseDTO.builder().staffId("STAFF-101").firstName("David").build());

        StaffResponseDTO response = client.getStaffById("STAFF-101");

        assertEquals("David", response.getFirstName());
    }

    @Test
    void getStaffById_withMissingStaff_throwsStaffNotFoundException() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        StaffDomainClient client = new StaffDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "staffServiceBaseUrl", "http://staff-service");

        mockWebClientNotFound(webClientBuilder, "http://staff-service", "/api/v1/staff/{id}", "STAFF-404", StaffResponseDTO.class);

        assertThrows(StaffNotFoundException.class, () -> client.getStaffById("STAFF-404"));
    }

    @Test
    void getAssetById_withExistingAsset_returnsAsset() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        AssetDomainClient client = new AssetDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "assetServiceBaseUrl", "http://asset-service");

        mockWebClient(webClientBuilder, "http://asset-service", "/api/v1/assets/{id}", "AST-5001",
                AssetResponseDTO.class,
                AssetResponseDTO.builder().assetId("AST-5001").type("LAPTOP").build());

        AssetResponseDTO response = client.getAssetById("AST-5001");

        assertEquals("LAPTOP", response.getType());
    }

    @Test
    void getAssetById_withMissingAsset_throwsAssetNotFoundException() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        AssetDomainClient client = new AssetDomainClient(webClientBuilder);
        ReflectionTestUtils.setField(client, "assetServiceBaseUrl", "http://asset-service");

        mockWebClientNotFound(webClientBuilder, "http://asset-service", "/api/v1/assets/{id}", "AST-404", AssetResponseDTO.class);

        assertThrows(AssetNotFoundException.class, () -> client.getAssetById("AST-404"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> void mockWebClient(WebClient.Builder webClientBuilder,
                                   String baseUrl,
                                   String path,
                                   String id,
                                   Class<T> responseClass,
                                   T response) {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(baseUrl + path, id)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(responseClass)).thenReturn(Mono.just(response));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> void mockWebClientNotFound(WebClient.Builder webClientBuilder,
                                           String baseUrl,
                                           String path,
                                           String id,
                                           Class<T> responseClass) {
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(baseUrl + path, id)).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(responseClass)).thenThrow(WebClientResponseException.create(
                404,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        ));
    }
}
