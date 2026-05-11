package com.itservice.apigatewa.apigateway.domainclientlayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itservice.apigatewa.apigateway.domainclientlayer.asset.AssetServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.customer.CustomerServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.ResolutionStepServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.TicketServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.staff.StaffServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetType;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.Priority;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRole;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GatewayDomainClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        mapper = new ObjectMapper();
    }

    @Test
    void assetServiceClient_callsAssetEndpointsAndHandlesErrors() {
        AssetServiceClient client = new AssetServiceClient(restTemplate, mapper, "localhost", "7003");
        String baseUrl = "http://localhost:7003/api/v1/assets";

        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withSuccess("[{\"assetId\":\"AST-5001\",\"type\":\"LAPTOP\",\"status\":\"IN_SERVICE\"}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getAllAssets().size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-5001")).andExpect(method(GET))
                .andRespond(withSuccess("{\"assetId\":\"AST-5001\",\"type\":\"LAPTOP\",\"status\":\"IN_SERVICE\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("AST-5001", client.getAssetById("AST-5001").getAssetId());

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withSuccess("{\"assetId\":\"AST-5002\",\"type\":\"DESKTOP\",\"status\":\"IN_SERVICE\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("AST-5002", client.createAsset(assetRequest()).getAssetId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-5001")).andExpect(method(PUT))
                .andRespond(withSuccess("{\"assetId\":\"AST-5001\",\"type\":\"DESKTOP\",\"status\":\"IN_SERVICE\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals(AssetType.DESKTOP, client.updateAsset("AST-5001", assetRequest()).getType());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-5001")).andExpect(method(DELETE))
                .andRespond(withSuccess());
        client.deleteAsset("AST-5001");

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-404")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Asset not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getAssetById("AST-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid asset")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.createAsset(assetRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Assets not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, client::getAllAssets);

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-5001")).andExpect(method(PUT))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid asset")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.updateAsset("AST-5001", assetRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-404")).andExpect(method(DELETE))
                .andRespond(withStatus(NOT_FOUND).body(error("Asset not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.deleteAsset("AST-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/AST-400")).andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST).body(error("Bad asset request")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(RuntimeException.class, () -> client.getAssetById("AST-400"));

        server.verify();
    }

    @Test
    void customerServiceClient_callsCustomerEndpointsAndHandlesErrors() {
        CustomerServiceClient client = new CustomerServiceClient(restTemplate, mapper, "localhost", "7001");
        String baseUrl = "http://localhost:7001/api/v1/customers";

        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withSuccess("[{\"customerId\":\"CUST-001\",\"firstName\":\"Alice\"}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getAllCustomers().size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-001")).andExpect(method(GET))
                .andRespond(withSuccess("{\"customerId\":\"CUST-001\",\"firstName\":\"Alice\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("Alice", client.getCustomerById("CUST-001").getFirstName());

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withSuccess("{\"customerId\":\"CUST-002\",\"firstName\":\"Bob\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("CUST-002", client.createCustomer(customerRequest()).getCustomerId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-001")).andExpect(method(PUT))
                .andRespond(withSuccess("{\"customerId\":\"CUST-001\",\"firstName\":\"Alice\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("CUST-001", client.updateCustomer("CUST-001", customerRequest()).getCustomerId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-001")).andExpect(method(DELETE))
                .andRespond(withSuccess());
        client.deleteCustomer("CUST-001");

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-404")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Customer not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getCustomerById("CUST-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid customer")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.createCustomer(customerRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Customers not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, client::getAllCustomers);

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-001")).andExpect(method(PUT))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid customer")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.updateCustomer("CUST-001", customerRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-404")).andExpect(method(DELETE))
                .andRespond(withStatus(NOT_FOUND).body(error("Customer not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.deleteCustomer("CUST-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/CUST-400")).andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST).body(error("Bad customer request")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(RuntimeException.class, () -> client.getCustomerById("CUST-400"));

        server.verify();
    }

    @Test
    void staffServiceClient_callsStaffEndpointsAndHandlesErrors() {
        StaffServiceClient client = new StaffServiceClient(restTemplate, mapper, "localhost", "7002");
        String baseUrl = "http://localhost:7002/api/v1/staff";

        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withSuccess("[{\"staffId\":\"STAFF-101\",\"firstName\":\"David\",\"staffRole\":\"AGENT\"}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getAllStaff().size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-101")).andExpect(method(GET))
                .andRespond(withSuccess("{\"staffId\":\"STAFF-101\",\"firstName\":\"David\",\"staffRole\":\"AGENT\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("David", client.getStaffById("STAFF-101").getFirstName());

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withSuccess("{\"staffId\":\"STAFF-102\",\"firstName\":\"Elena\",\"staffRole\":\"ADMIN\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("STAFF-102", client.createStaff(staffRequest()).getStaffId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-101")).andExpect(method(PUT))
                .andRespond(withSuccess("{\"staffId\":\"STAFF-101\",\"firstName\":\"David\",\"staffRole\":\"AGENT\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("STAFF-101", client.updateStaff("STAFF-101", staffRequest()).getStaffId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-101")).andExpect(method(DELETE))
                .andRespond(withSuccess());
        client.deleteStaff("STAFF-101");

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-404")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Staff not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getStaffById("STAFF-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid staff")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.createStaff(staffRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Staff not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, client::getAllStaff);

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-101")).andExpect(method(PUT))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid staff")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.updateStaff("STAFF-101", staffRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-404")).andExpect(method(DELETE))
                .andRespond(withStatus(NOT_FOUND).body(error("Staff not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.deleteStaff("STAFF-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/STAFF-400")).andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST).body(error("Bad staff request")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(RuntimeException.class, () -> client.getStaffById("STAFF-400"));

        server.verify();
    }

    @Test
    void ticketServiceClient_callsTicketEndpointsAndHandlesErrors() {
        TicketServiceClient client = new TicketServiceClient(restTemplate, mapper, "localhost", "7004");
        String baseUrl = "http://localhost:7004/api/v1/tickets";

        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withSuccess("[{\"ticketId\":\"TIC-1001\",\"title\":\"Laptop issue\",\"status\":\"NEW\",\"priority\":\"HIGH\"}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getAllTickets().size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-1001")).andExpect(method(GET))
                .andRespond(withSuccess("{\"ticketId\":\"TIC-1001\",\"title\":\"Laptop issue\",\"status\":\"NEW\",\"priority\":\"HIGH\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("TIC-1001", client.getByTicketId("TIC-1001").getTicketId());

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withSuccess("{\"ticketId\":\"TIC-1002\",\"title\":\"Laptop issue\",\"status\":\"NEW\",\"priority\":\"HIGH\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals("TIC-1002", client.createTicket(ticketRequest()).getTicketId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-1001")).andExpect(method(PUT))
                .andRespond(withSuccess("{\"ticketId\":\"TIC-1001\",\"title\":\"Laptop issue\",\"status\":\"IN_PROGRESS\",\"priority\":\"HIGH\"}",
                        MediaType.APPLICATION_JSON));
        assertEquals(TicketStatus.IN_PROGRESS, client.updateTicket("TIC-1001", ticketRequest()).getStatus());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-1001")).andExpect(method(DELETE))
                .andRespond(withSuccess());
        client.deleteTicket("TIC-1001");

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-404")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Ticket not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getByTicketId("TIC-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(POST))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid ticket")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.createTicket(ticketRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl)).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Tickets not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, client::getAllTickets);

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-1001")).andExpect(method(PUT))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Invalid ticket")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class, () -> client.updateTicket("TIC-1001", ticketRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-404")).andExpect(method(DELETE))
                .andRespond(withStatus(NOT_FOUND).body(error("Ticket not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.deleteTicket("TIC-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/TIC-400")).andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST).body(error("Bad ticket request")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(RuntimeException.class, () -> client.getByTicketId("TIC-400"));

        server.verify();
    }

    @Test
    void resolutionStepServiceClient_callsResolutionStepEndpointsAndHandlesErrors() {
        ResolutionStepServiceClient client = new ResolutionStepServiceClient(restTemplate, mapper, "localhost", "7004");
        String baseUrl = "http://localhost:7004";

        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step")).andExpect(method(GET))
                .andRespond(withSuccess("[{\"stepId\":\"STEP-1001\",\"ticketId\":\"TIC-1001\",\"stepNumber\":1}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getAllResolutionSteps().size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step/STEP-1001")).andExpect(method(GET))
                .andRespond(withSuccess("{\"stepId\":\"STEP-1001\",\"ticketId\":\"TIC-1001\",\"stepNumber\":1}",
                        MediaType.APPLICATION_JSON));
        assertEquals("STEP-1001", client.getResolutionStepById("STEP-1001").getStepId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/tickets/TIC-1001/resolution-step")).andExpect(method(GET))
                .andRespond(withSuccess("[{\"stepId\":\"STEP-1001\",\"ticketId\":\"TIC-1001\",\"stepNumber\":1}]",
                        MediaType.APPLICATION_JSON));
        assertEquals(1, client.getResolutionStepsByTicketId("TIC-1001").size());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/tickets/TIC-1001/resolution-step")).andExpect(method(POST))
                .andRespond(withSuccess("{\"stepId\":\"STEP-1002\",\"ticketId\":\"TIC-1001\",\"stepNumber\":2}",
                        MediaType.APPLICATION_JSON));
        assertEquals("STEP-1002", client.createResolutionStep("TIC-1001", resolutionStepRequest()).getStepId());

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step/STEP-1001")).andExpect(method(DELETE))
                .andRespond(withSuccess());
        client.deleteResolutionStep("STEP-1001");

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step/STEP-404")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Resolution step not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getResolutionStepById("STEP-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/tickets/TIC-1001/resolution-step")).andExpect(method(POST))
                .andRespond(withStatus(UNPROCESSABLE_ENTITY).body(error("Ticket is closed")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(InvalidInputException.class,
                () -> client.createResolutionStep("TIC-1001", resolutionStepRequest()));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Resolution steps not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, client::getAllResolutionSteps);

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/tickets/TIC-404/resolution-step")).andExpect(method(GET))
                .andRespond(withStatus(NOT_FOUND).body(error("Ticket not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.getResolutionStepsByTicketId("TIC-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step/STEP-404")).andExpect(method(DELETE))
                .andRespond(withStatus(NOT_FOUND).body(error("Resolution step not found")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(NotFoundException.class, () -> client.deleteResolutionStep("STEP-404"));

        resetServer();
        server.expect(once(), requestTo(baseUrl + "/api/v1/resolution-step/STEP-400")).andExpect(method(GET))
                .andRespond(withStatus(BAD_REQUEST).body(error("Bad resolution step request")).contentType(MediaType.APPLICATION_JSON));
        assertThrows(RuntimeException.class, () -> client.getResolutionStepById("STEP-400"));

        server.verify();
    }

    private void resetServer() {
        server.verify();
        server.reset();
    }

    private String error(String message) {
        return "{\"timestamp\":\"2026-05-11T00:00:00Z\",\"path\":\"uri=/test\",\"httpStatus\":\"NOT_FOUND\",\"message\":\"" + message + "\"}";
    }

    private AssetRequestDTO assetRequest() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.DESKTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);
        return requestDTO;
    }

    private CustomerRequestDTO customerRequest() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.j@company.com");
        requestDTO.setDepartment("Marketing");
        return requestDTO;
    }

    private StaffRequestDTO staffRequest() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("David");
        requestDTO.setLastName("Miller");
        requestDTO.setEmail("d.miller@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);
        return requestDTO;
    }

    private TicketRequestDTO ticketRequest() {
        TicketRequestDTO requestDTO = new TicketRequestDTO();
        requestDTO.setTitle("Laptop issue");
        requestDTO.setDescription("Laptop will not boot");
        requestDTO.setCustomerId("CUST-001");
        requestDTO.setStaffId("STAFF-101");
        requestDTO.setAssetId("AST-5001");
        requestDTO.setStatus(TicketStatus.NEW);
        requestDTO.setPriority(Priority.HIGH);
        return requestDTO;
    }

    private ResolutionStepRequestDTO resolutionStepRequest() {
        ResolutionStepRequestDTO requestDTO = new ResolutionStepRequestDTO();
        requestDTO.setStepNumber(1);
        requestDTO.setActionTaken("Checked the device");
        requestDTO.setResult("Device needs repair");
        return requestDTO;
    }
}
