package com.itservice.customermanagement.customermanagement.businesslayer;


import com.itservice.customermanagement.customermanagement.dataaccesslayer.CustomerRepository;
import com.itservice.customermanagement.customermanagement.datamappinglayer.CustomerMapper;

import com.itservice.customermanagement.customermanagement.domain.Customer;
import com.itservice.customermanagement.customermanagement.domain.CustomerIdentifier;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerRequestDTO;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerResponseDTO;

import com.itservice.customermanagement.customermanagement.utilities.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CustomerService {
private final CustomerRepository customerRepository;
private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO getByCustomerId(String customerId) {
        Customer customer = customerRepository.findCustomerByCustomerIdentifier_CustomerId(customerId);
        if (customer == null) {
            throw new NotFoundException("Customer not found : "+customerId);
        }
        return customerMapper.toDTO(customer);
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        CustomerIdentifier customerIdentifier = new CustomerIdentifier();
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        customer.setCustomerIdentifier(customerIdentifier);
        Customer persistedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(persistedCustomer);
    }

    public CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO customerRequestDTO)
    {
        Customer customer = customerRepository.findCustomerByCustomerIdentifier_CustomerId(customerId);
        if (customer == null) {
            throw new NotFoundException("Customer not found : "+customerId);
        }
        Customer requestCustomer = customerMapper.toEntity(customerRequestDTO);
        requestCustomer.setId(customer.getId());
        requestCustomer.setCustomerIdentifier(customer.getCustomerIdentifier());
        Customer persistedCustomer = customerRepository.save(requestCustomer);
        return customerMapper.toDTO(persistedCustomer);
    }

    public void deleteCustomer(String customerId) {
        Customer customer = customerRepository.findCustomerByCustomerIdentifier_CustomerId(customerId);
        if(customer == null) {
            throw new NotFoundException("Customer not found : "+customerId);
        }
        customerRepository.deleteCustomerByCustomerIdentifier_CustomerId(customerId);
    }
}
