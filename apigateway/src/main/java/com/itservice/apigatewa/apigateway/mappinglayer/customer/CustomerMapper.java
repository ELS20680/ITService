package com.itservice.apigatewa.apigateway.mappinglayer.customer;

import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerController;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerResponseDTO;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerMapper {
//    public CustomerResponseDTO toDTO(Customer customer) {
//        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
//        customerResponseDTO.setCustomerId(customer.getCustomerIdentifier().getCustomerId());
//        customerResponseDTO.setFirstName(customer.getFirstName());
//        customerResponseDTO.setLastName(customer.getLastName());
//        customerResponseDTO.setEmail(customer.getEmail());
//        customerResponseDTO.setDepartment(customer.getDepartment());
//
//        Link all = linkTo(methodOn(CustomerController.class).getAll()).withRel("all Customers");
//        customerResponseDTO.add(all);
//
//        return customerResponseDTO;
//    }
//
//    public Customer toEntity(CustomerRequestDTO customerRequestDTO) {
//        Customer customer = new Customer();
//        customer.setFirstName(customerRequestDTO.getFirstName());
//        customer.setLastName(customerRequestDTO.getLastName());
//        customer.setEmail(customerRequestDTO.getEmail());
//        customer.setDepartment(customerRequestDTO.getDepartment());
//
//        return customer;
//    }
}