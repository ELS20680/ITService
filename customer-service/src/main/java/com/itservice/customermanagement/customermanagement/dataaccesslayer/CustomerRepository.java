package com.itservice.customermanagement.customermanagement.dataaccesslayer;

import com.itservice.customermanagement.customermanagement.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findCustomerByCustomerIdentifier_CustomerId(String customerIdentifierCustomerId);
    void deleteCustomerByCustomerIdentifier_CustomerId(String customerIdentifierCustomerId);
}
