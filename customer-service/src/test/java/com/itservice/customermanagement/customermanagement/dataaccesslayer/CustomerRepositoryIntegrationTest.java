package com.itservice.customermanagement.customermanagement.dataaccesslayer;

import com.itservice.customermanagement.customermanagement.domain.Customer;
import com.itservice.customermanagement.customermanagement.domain.CustomerIdentifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@ActiveProfiles("testing-profile")
class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findCustomerByCustomerIdentifier_CustomerId_withExistingCustomer_returnsCustomer() {
        Customer customer = Customer.builder()
                .customerIdentifier(new CustomerIdentifier("CUST-9001"))
                .firstName("Maya")
                .lastName("Chen")
                .email("maya.chen@company.com")
                .department("Support")
                .build();
        customerRepository.save(customer);

        Customer foundCustomer = customerRepository.findCustomerByCustomerIdentifier_CustomerId("CUST-9001");

        assertNotNull(foundCustomer);
        assertEquals("Maya", foundCustomer.getFirstName());
    }

    @Test
    void findCustomerByCustomerIdentifier_CustomerId_withMissingCustomer_returnsNull() {
        Customer foundCustomer = customerRepository.findCustomerByCustomerIdentifier_CustomerId("CUST-9999");

        assertNull(foundCustomer);
    }
}
