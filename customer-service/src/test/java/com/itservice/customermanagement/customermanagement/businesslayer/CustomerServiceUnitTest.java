package com.itservice.customermanagement.customermanagement.businesslayer;

import com.itservice.customermanagement.customermanagement.dataaccesslayer.CustomerRepository;
import com.itservice.customermanagement.customermanagement.datamappinglayer.CustomerMapper;
import com.itservice.customermanagement.customermanagement.domain.Customer;
import com.itservice.customermanagement.customermanagement.domain.CustomerIdentifier;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerRequestDTO;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerResponseDTO;
import com.itservice.customermanagement.customermanagement.utilities.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setup() {
        customerService = new CustomerService(customerRepository, new CustomerMapper());
    }

    @Test
    void getAllCustomers_returnsMappedCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(customer("CUST-1001")));

        List<CustomerResponseDTO> customers = customerService.getAllCustomers();

        assertEquals(1, customers.size());
        assertEquals("CUST-1001", customers.get(0).getCustomerId());
    }

    @Test
    void getByCustomerId_withExistingCustomer_returnsCustomer() {
        when(customerRepository.findCustomerByCustomerIdentifier_CustomerId("CUST-1001")).thenReturn(customer("CUST-1001"));

        CustomerResponseDTO customer = customerService.getByCustomerId("CUST-1001");

        assertEquals("Alice", customer.getFirstName());
    }

    @Test
    void getByCustomerId_withMissingCustomer_throwsNotFoundException() {
        when(customerRepository.findCustomerByCustomerIdentifier_CustomerId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> customerService.getByCustomerId("bad-id"));
    }

    @Test
    void createCustomer_savesCustomerAndReturnsResponse() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            return customer;
        });

        CustomerResponseDTO response = customerService.createCustomer(request());

        assertNotNull(response.getCustomerId());
        assertEquals("Engineering", response.getDepartment());
    }

    @Test
    void updateCustomer_withExistingCustomer_keepsCustomerId() {
        when(customerRepository.findCustomerByCustomerIdentifier_CustomerId("CUST-1001")).thenReturn(customer("CUST-1001"));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponseDTO response = customerService.updateCustomer("CUST-1001", request());

        assertEquals("CUST-1001", response.getCustomerId());
    }

    @Test
    void deleteCustomer_withMissingCustomer_throwsNotFoundException() {
        when(customerRepository.findCustomerByCustomerIdentifier_CustomerId("bad-id")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> customerService.deleteCustomer("bad-id"));
    }

    @Test
    void deleteCustomer_withExistingCustomer_deletesCustomer() {
        Customer customer = customer("CUST-1001");
        when(customerRepository.findCustomerByCustomerIdentifier_CustomerId("CUST-1001")).thenReturn(customer);

        customerService.deleteCustomer("CUST-1001");

        verify(customerRepository).delete(customer);
    }

    private Customer customer(String customerId) {
        return Customer.builder()
                .id(1L)
                .customerIdentifier(new CustomerIdentifier(customerId))
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.j@company.com")
                .department("Engineering")
                .build();
    }

    private CustomerRequestDTO request() {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO();
        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.j@company.com");
        requestDTO.setDepartment("Engineering");
        return requestDTO;
    }
}
