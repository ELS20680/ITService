package com.itservice.incidentresolution.domainclientlayer;

import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetRequestDTO;
import com.itservice.incidentresolution.presentationlayer.assetDTO.AssetResponseDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerRequestDTO;
import com.itservice.incidentresolution.presentationlayer.customerDTO.CustomerResponseDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffRequestDTO;
import com.itservice.incidentresolution.presentationlayer.staffDTO.StaffResponseDTO;
import com.itservice.incidentresolution.utilities.AssetNotFoundException;
import com.itservice.incidentresolution.utilities.CustomerNotFoundException;
import com.itservice.incidentresolution.utilities.StaffNotFoundException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    void assetClient_withWriteOperations_callsAssetService() throws IOException {
        HttpServer server = startServer(exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            readBody(exchange);

            if ("GET".equals(method) && "/api/v1/assets".equals(path)) {
                writeJson(exchange, 200, "[{\"assetId\":\"AST-1\",\"type\":\"LAPTOP\",\"status\":\"IN_SERVICE\"}]");
            } else if ("POST".equals(method) && "/api/v1/assets".equals(path)) {
                writeJson(exchange, 201, "{\"assetId\":\"AST-2\",\"type\":\"DESKTOP\",\"status\":\"IN_SERVICE\"}");
            } else if ("PUT".equals(method) && "/api/v1/assets/AST-2".equals(path)) {
                writeJson(exchange, 200, "{\"assetId\":\"AST-2\",\"type\":\"DESKTOP\",\"status\":\"IN_REPAIR\"}");
            } else if ("DELETE".equals(method) && "/api/v1/assets/AST-2".equals(path)) {
                writeJson(exchange, 204, "");
            } else {
                writeJson(exchange, 404, "");
            }
        });

        try {
            AssetDomainClient client = new AssetDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "assetServiceBaseUrl", serverBaseUrl(server));
            AssetRequestDTO request = new AssetRequestDTO();
            request.setType("DESKTOP");
            request.setStatus("IN_SERVICE");

            List<AssetResponseDTO> assets = client.getAllAssets();
            AssetResponseDTO created = client.createAsset(request);
            AssetResponseDTO updated = client.updateAsset("AST-2", request);

            assertEquals(1, assets.size());
            assertEquals("AST-2", created.getAssetId());
            assertEquals("IN_REPAIR", updated.getStatus());
            assertDoesNotThrow(() -> client.deleteAsset("AST-2"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void assetClient_withMissingAssetOnWriteOperations_throwsAssetNotFoundException() throws IOException {
        HttpServer server = startServer(exchange -> writeJson(exchange, 404, ""));

        try {
            AssetDomainClient client = new AssetDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "assetServiceBaseUrl", serverBaseUrl(server));
            AssetRequestDTO request = new AssetRequestDTO();

            assertThrows(AssetNotFoundException.class, () -> client.updateAsset("AST-404", request));
            assertThrows(AssetNotFoundException.class, () -> client.deleteAsset("AST-404"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void customerClient_withWriteOperations_callsCustomerService() throws IOException {
        HttpServer server = startServer(exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            readBody(exchange);

            if ("GET".equals(method) && "/api/v1/customers".equals(path)) {
                writeJson(exchange, 200, "[{\"customerId\":\"CUST-1\",\"firstName\":\"Alice\",\"lastName\":\"Jones\",\"email\":\"alice@example.com\",\"department\":\"IT\"}]");
            } else if ("POST".equals(method) && "/api/v1/customers".equals(path)) {
                writeJson(exchange, 201, "{\"customerId\":\"CUST-2\",\"firstName\":\"Bob\",\"lastName\":\"Jones\",\"email\":\"bob@example.com\",\"department\":\"IT\"}");
            } else if ("PUT".equals(method) && "/api/v1/customers/CUST-2".equals(path)) {
                writeJson(exchange, 200, "{\"customerId\":\"CUST-2\",\"firstName\":\"Bobby\",\"lastName\":\"Jones\",\"email\":\"bob@example.com\",\"department\":\"Support\"}");
            } else if ("DELETE".equals(method) && "/api/v1/customers/CUST-2".equals(path)) {
                writeJson(exchange, 204, "");
            } else {
                writeJson(exchange, 404, "");
            }
        });

        try {
            CustomerDomainClient client = new CustomerDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "customerServiceBaseUrl", serverBaseUrl(server));
            CustomerRequestDTO request = new CustomerRequestDTO();
            request.setFirstName("Bob");
            request.setLastName("Jones");
            request.setEmail("bob@example.com");
            request.setDepartment("IT");

            List<CustomerResponseDTO> customers = client.getAllCustomers();
            CustomerResponseDTO created = client.createCustomer(request);
            CustomerResponseDTO updated = client.updateCustomer("CUST-2", request);

            assertEquals(1, customers.size());
            assertEquals("CUST-2", created.getCustomerId());
            assertEquals("Support", updated.getDepartment());
            assertDoesNotThrow(() -> client.deleteCustomer("CUST-2"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void customerClient_withMissingCustomerOnWriteOperations_throwsCustomerNotFoundException() throws IOException {
        HttpServer server = startServer(exchange -> writeJson(exchange, 404, ""));

        try {
            CustomerDomainClient client = new CustomerDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "customerServiceBaseUrl", serverBaseUrl(server));
            CustomerRequestDTO request = new CustomerRequestDTO();

            assertThrows(CustomerNotFoundException.class, () -> client.updateCustomer("CUST-404", request));
            assertThrows(CustomerNotFoundException.class, () -> client.deleteCustomer("CUST-404"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void staffClient_withWriteOperations_callsStaffService() throws IOException {
        HttpServer server = startServer(exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            readBody(exchange);

            if ("GET".equals(method) && "/api/v1/staff".equals(path)) {
                writeJson(exchange, 200, "[{\"staffId\":\"STAFF-1\",\"firstName\":\"David\",\"lastName\":\"Kim\",\"staffEmail\":\"david@example.com\",\"staffRole\":\"AGENT\"}]");
            } else if ("POST".equals(method) && "/api/v1/staff".equals(path)) {
                writeJson(exchange, 201, "{\"staffId\":\"STAFF-2\",\"firstName\":\"Emma\",\"lastName\":\"Kim\",\"staffEmail\":\"emma@example.com\",\"staffRole\":\"AGENT\"}");
            } else if ("PUT".equals(method) && "/api/v1/staff/STAFF-2".equals(path)) {
                writeJson(exchange, 200, "{\"staffId\":\"STAFF-2\",\"firstName\":\"Emma\",\"lastName\":\"Kim\",\"staffEmail\":\"emma@example.com\",\"staffRole\":\"ADMIN\"}");
            } else if ("DELETE".equals(method) && "/api/v1/staff/STAFF-2".equals(path)) {
                writeJson(exchange, 204, "");
            } else {
                writeJson(exchange, 404, "");
            }
        });

        try {
            StaffDomainClient client = new StaffDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "staffServiceBaseUrl", serverBaseUrl(server));
            StaffRequestDTO request = new StaffRequestDTO();
            request.setFistName("Emma");
            request.setLastName("Kim");
            request.setEmail("emma@example.com");
            request.setStaffRole("AGENT");

            List<StaffResponseDTO> staff = client.getAllStaff();
            StaffResponseDTO created = client.createStaff(request);
            StaffResponseDTO updated = client.updateStaff("STAFF-2", request);

            assertEquals(1, staff.size());
            assertEquals("STAFF-2", created.getStaffId());
            assertEquals("ADMIN", updated.getStaffRole());
            assertDoesNotThrow(() -> client.deleteStaff("STAFF-2"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void staffClient_withMissingStaffOnWriteOperations_throwsStaffNotFoundException() throws IOException {
        HttpServer server = startServer(exchange -> writeJson(exchange, 404, ""));

        try {
            StaffDomainClient client = new StaffDomainClient(WebClient.builder());
            ReflectionTestUtils.setField(client, "staffServiceBaseUrl", serverBaseUrl(server));
            StaffRequestDTO request = new StaffRequestDTO();

            assertThrows(StaffNotFoundException.class, () -> client.updateStaff("STAFF-404", request));
            assertThrows(StaffNotFoundException.class, () -> client.deleteStaff("STAFF-404"));
        } finally {
            server.stop(0);
        }
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

    private HttpServer startServer(HttpHandler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/", handler);
        server.start();
        return server;
    }

    private String serverBaseUrl(HttpServer server) {
        return "http://localhost:" + server.getAddress().getPort();
    }

    private void readBody(HttpExchange exchange) throws IOException {
        exchange.getRequestBody().readAllBytes();
    }

    private void writeJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        if (status == 204) {
            exchange.sendResponseHeaders(status, -1);
            return;
        }
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
