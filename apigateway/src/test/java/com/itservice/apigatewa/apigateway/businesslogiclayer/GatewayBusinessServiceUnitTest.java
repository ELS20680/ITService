package com.itservice.apigatewa.apigateway.businesslogiclayer;

import com.itservice.apigatewa.apigateway.businesslogiclayer.asset.AssetService;
import com.itservice.apigatewa.apigateway.businesslogiclayer.customer.CustomerService;
import com.itservice.apigatewa.apigateway.businesslogiclayer.incident.ResolutionStepService;
import com.itservice.apigatewa.apigateway.businesslogiclayer.incident.TicketService;
import com.itservice.apigatewa.apigateway.businesslogiclayer.staff.StaffService;
import com.itservice.apigatewa.apigateway.domainclientlayer.asset.AssetServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.customer.CustomerServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.ResolutionStepServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.incident.TicketServiceClient;
import com.itservice.apigatewa.apigateway.domainclientlayer.staff.StaffServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.asset.AssetType;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.Priority;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.ResolutionStepResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.incident.TicketStatus;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffResponseDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.staff.StaffRole;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import com.itservice.apigatewa.apigateway.utilities.NotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GatewayBusinessServiceUnitTest {

    @Test
    void assetServiceUnit_addsLinksAndHandlesInvalidInput() {
        AssetServiceClient client = mock(AssetServiceClient.class);
        AssetService service = new AssetService(client);

        when(client.getAllAssets()).thenReturn(List.of(assetResponse("AST-5001")));
        when(client.getAssetById("AST-5001")).thenReturn(assetResponse("AST-5001"));
        when(client.createAsset(any(AssetRequestDTO.class))).thenReturn(assetResponse("AST-5002"));
        when(client.updateAsset(eq("AST-5001"), any(AssetRequestDTO.class))).thenReturn(assetResponse("AST-5001"));
        when(client.createAsset(null)).thenReturn(null);
        when(client.updateAsset("bad-id", null)).thenReturn(null);

        assertEquals(1, service.getAllAssets().size());
        assertNotNull(service.getAssetById("AST-5001").getLink("self").orElse(null));
        assertEquals("AST-5002", service.createAsset(assetRequest()).getAssetId());
        assertEquals("AST-5001", service.updateAsset("AST-5001", assetRequest()).getAssetId());
        assertThrows(InvalidInputException.class, () -> service.createAsset(null));
        assertThrows(InvalidInputException.class, () -> service.updateAsset("bad-id", null));

        service.deleteAsset("AST-5001");
        verify(client).deleteAsset("AST-5001");
    }

    @Test
    void customerServiceUnit_addsLinksAndHandlesInvalidInput() {
        CustomerServiceClient client = mock(CustomerServiceClient.class);
        CustomerService service = new CustomerService(client);

        when(client.getAllCustomers()).thenReturn(List.of(customerResponse("CUST-001")));
        when(client.getCustomerById("CUST-001")).thenReturn(customerResponse("CUST-001"));
        when(client.createCustomer(any(CustomerRequestDTO.class))).thenReturn(customerResponse("CUST-002"));
        when(client.updateCustomer(eq("CUST-001"), any(CustomerRequestDTO.class))).thenReturn(customerResponse("CUST-001"));
        when(client.createCustomer(null)).thenReturn(null);
        when(client.updateCustomer("bad-id", null)).thenReturn(null);

        assertEquals(1, service.getAllCustomers().size());
        assertNotNull(service.getCustomerById("CUST-001").getLink("self").orElse(null));
        assertEquals("CUST-002", service.createCustomer(customerRequest()).getCustomerId());
        assertEquals("CUST-001", service.updateCustomer("CUST-001", customerRequest()).getCustomerId());
        assertThrows(InvalidInputException.class, () -> service.createCustomer(null));
        assertThrows(InvalidInputException.class, () -> service.updateCustomer("bad-id", null));

        service.deleteCustomer("CUST-001");
        verify(client).deleteCustomer("CUST-001");
    }

    @Test
    void staffServiceUnit_addsLinksAndHandlesInvalidInput() {
        StaffServiceClient client = mock(StaffServiceClient.class);
        StaffService service = new StaffService(client);

        when(client.getAllStaff()).thenReturn(List.of(staffResponse("STAFF-101")));
        when(client.getStaffById("STAFF-101")).thenReturn(staffResponse("STAFF-101"));
        when(client.createStaff(any(StaffRequestDTO.class))).thenReturn(staffResponse("STAFF-102"));
        when(client.updateStaff(eq("STAFF-101"), any(StaffRequestDTO.class))).thenReturn(staffResponse("STAFF-101"));
        when(client.createStaff(null)).thenReturn(null);
        when(client.updateStaff("bad-id", null)).thenReturn(null);

        assertEquals(1, service.getAllStaff().size());
        assertNotNull(service.getStaffById("STAFF-101").getLink("self").orElse(null));
        assertEquals("STAFF-102", service.createStaff(staffRequest()).getStaffId());
        assertEquals("STAFF-101", service.updateStaff("STAFF-101", staffRequest()).getStaffId());
        assertThrows(InvalidInputException.class, () -> service.createStaff(null));
        assertThrows(InvalidInputException.class, () -> service.updateStaff("bad-id", null));

        service.deleteStaff("STAFF-101");
        verify(client).deleteStaff("STAFF-101");
    }

    @Test
    void ticketServiceUnit_addsLinksAndHandlesInvalidInput() {
        TicketServiceClient client = mock(TicketServiceClient.class);
        TicketService service = new TicketService(client);

        when(client.getAllTickets()).thenReturn(List.of(ticketResponse("TIC-1001")));
        when(client.getByTicketId("TIC-1001")).thenReturn(ticketResponse("TIC-1001"));
        when(client.createTicket(any(TicketRequestDTO.class))).thenReturn(ticketResponse("TIC-1002"));
        when(client.updateTicket(eq("TIC-1001"), any(TicketRequestDTO.class))).thenReturn(ticketResponse("TIC-1001"));
        when(client.createTicket(null)).thenReturn(null);
        when(client.updateTicket("bad-id", null)).thenReturn(null);

        assertEquals(1, service.getAllTickets().size());
        assertNotNull(service.getByTicketId("TIC-1001").getLink("self").orElse(null));
        assertEquals("TIC-1002", service.createTicket(ticketRequest()).getTicketId());
        assertEquals("TIC-1001", service.updateTicket("TIC-1001", ticketRequest()).getTicketId());
        assertThrows(InvalidInputException.class, () -> service.createTicket(null));
        assertThrows(InvalidInputException.class, () -> service.updateTicket("bad-id", null));

        service.deleteTicket("TIC-1001");
        verify(client).deleteTicket("TIC-1001");
    }

    @Test
    void resolutionStepServiceUnit_addsLinksAndHandlesInvalidInput() {
        ResolutionStepServiceClient client = mock(ResolutionStepServiceClient.class);
        ResolutionStepService service = new ResolutionStepService(client);

        when(client.getAllResolutionSteps()).thenReturn(List.of(resolutionStepResponse("STEP-1001")));
        when(client.getResolutionStepById("STEP-1001")).thenReturn(resolutionStepResponse("STEP-1001"));
        when(client.getResolutionStepsByTicketId("TIC-1001")).thenReturn(List.of(resolutionStepResponse("STEP-1001")));
        when(client.createResolutionStep(eq("TIC-1001"), any(ResolutionStepRequestDTO.class)))
                .thenReturn(resolutionStepResponse("STEP-1002"));
        when(client.createResolutionStep("bad-id", null)).thenReturn(null);

        assertEquals(1, service.getAllResolutionSteps().size());
        assertNotNull(service.getResolutionStepById("STEP-1001").getLink("self").orElse(null));
        assertEquals(1, service.getResolutionStepsByTicketId("TIC-1001").size());
        assertEquals("STEP-1002", service.createResolutionStep("TIC-1001", resolutionStepRequest()).getStepId());
        assertThrows(InvalidInputException.class, () -> service.createResolutionStep("bad-id", null));

        service.deleteResolutionStep("STEP-1001");
        verify(client).deleteResolutionStep("STEP-1001");
    }

    @Test
    void gatewayExceptions_haveAllConstructorsCovered() {
        assertNotNull(new InvalidInputException());
        assertNotNull(new NotFoundException());
        assertEquals("invalid", new InvalidInputException("invalid").getMessage());
        assertEquals("missing", new NotFoundException("missing").getMessage());
        assertEquals("invalid cause", new InvalidInputException(new RuntimeException("invalid cause")).getCause().getMessage());
        assertEquals("missing cause", new NotFoundException(new RuntimeException("missing cause")).getCause().getMessage());
        assertEquals("invalid", new InvalidInputException("invalid", new RuntimeException()).getMessage());
        assertEquals("missing", new NotFoundException("missing", new RuntimeException()).getMessage());
    }

    private AssetRequestDTO assetRequest() {
        AssetRequestDTO requestDTO = new AssetRequestDTO();
        requestDTO.setType(AssetType.DESKTOP);
        requestDTO.setStatus(AssetStatus.IN_SERVICE);
        return requestDTO;
    }

    private AssetResponseDTO assetResponse(String assetId) {
        return AssetResponseDTO.builder()
                .assetId(assetId)
                .type(AssetType.DESKTOP)
                .status(AssetStatus.IN_SERVICE)
                .build();
    }

    private CustomerRequestDTO customerRequest() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.j@company.com");
        requestDTO.setDepartment("Marketing");
        return requestDTO;
    }

    private CustomerResponseDTO customerResponse(String customerId) {
        return CustomerResponseDTO.builder()
                .customerId(customerId)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.j@company.com")
                .department("Marketing")
                .build();
    }

    private StaffRequestDTO staffRequest() {
        StaffRequestDTO requestDTO = new StaffRequestDTO();
        requestDTO.setFistName("David");
        requestDTO.setLastName("Miller");
        requestDTO.setEmail("d.miller@it-support.com");
        requestDTO.setStaffRole(StaffRole.AGENT);
        return requestDTO;
    }

    private StaffResponseDTO staffResponse(String staffId) {
        return StaffResponseDTO.builder()
                .staffId(staffId)
                .firstName("David")
                .lastName("Miller")
                .staffEmail("d.miller@it-support.com")
                .staffRole(StaffRole.AGENT)
                .build();
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

    private TicketResponseDTO ticketResponse(String ticketId) {
        return TicketResponseDTO.builder()
                .ticketId(ticketId)
                .title("Laptop issue")
                .description("Laptop will not boot")
                .customerId("CUST-001")
                .staffId("STAFF-101")
                .assetId("AST-5001")
                .status(TicketStatus.NEW)
                .priority(Priority.HIGH)
                .build();
    }

    private ResolutionStepRequestDTO resolutionStepRequest() {
        ResolutionStepRequestDTO requestDTO = new ResolutionStepRequestDTO();
        requestDTO.setStepNumber(1);
        requestDTO.setActionTaken("Checked the device");
        requestDTO.setResult("Device needs repair");
        return requestDTO;
    }

    private ResolutionStepResponseDTO resolutionStepResponse(String stepId) {
        return ResolutionStepResponseDTO.builder()
                .stepId(stepId)
                .ticketId("TIC-1001")
                .stepNumber(1)
                .actionTaken("Checked the device")
                .result("Device needs repair")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
