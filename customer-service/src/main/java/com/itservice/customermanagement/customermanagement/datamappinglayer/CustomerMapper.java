package com.itservice.customermanagement.customermanagement.datamappinglayer;

import com.itservice.customermanagement.customermanagement.domain.Customer;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerController;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerRequestDTO;
import com.itservice.customermanagement.customermanagement.presentationlayer.CustomerResponseDTO;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerMapper {
    public CustomerResponseDTO toDTO(Customer customer) {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setCustomerId(customer.getCustomerIdentifier().getCustomerId());
        customerResponseDTO.setFirstName(customer.getFirstName());
        customerResponseDTO.setLastName(customer.getLastName());
        customerResponseDTO.setEmail(customer.getEmail());
        customerResponseDTO.setDepartment(customer.getDepartment());


        return customerResponseDTO;
    }

    public Customer toEntity(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setDepartment(customerRequestDTO.getDepartment());

        return customer;
    }
}