package com.itservice.apigatewa.apigateway.businesslogiclayer.customer;

import com.itservice.apigatewa.apigateway.domainclientlayer.customer.CustomerServiceClient;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerController;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerRequestDTO;
import com.itservice.apigatewa.apigateway.presentationlayer.customer.CustomerResponseDTO;
import com.itservice.apigatewa.apigateway.utilities.InvalidInputException;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class CustomerService {
    //inject
    private final CustomerServiceClient customerServiceClient;

    public CustomerService(CustomerServiceClient customerServiceClient) {
        this.customerServiceClient = customerServiceClient;
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        List<CustomerResponseDTO> customerResponseDTOS =customerServiceClient.getAllCustomers();
        if(customerResponseDTOS != null) {
            for (CustomerResponseDTO customerResponseDTO : customerResponseDTOS) {
                addLinks(customerResponseDTO);
            }
        }
        return customerResponseDTOS;
    }

    public CustomerResponseDTO getCustomerById(String customerId) {
        CustomerResponseDTO customerResponseDTO = customerServiceClient.getCustomerById(customerId);
        if(customerResponseDTO != null) {
            addLinks(customerResponseDTO);
        }
        return customerResponseDTO;
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerServiceClient.createCustomer(customerRequestDTO);
        if(customerResponseDTO != null) {
            addLinks(customerResponseDTO);
            return customerResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + customerRequestDTO);
    }

    public CustomerResponseDTO updateCustomer(String customerId, CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerServiceClient.updateCustomer(customerId, customerRequestDTO);
        if(customerResponseDTO != null) {
            addLinks(customerResponseDTO);
            return customerResponseDTO;
        }
        throw new InvalidInputException("Invalid input " + customerRequestDTO);
    }

    public void deleteCustomer(String customerId) {
        customerServiceClient.deleteCustomer(customerId);
    }

    private CustomerResponseDTO addLinks(CustomerResponseDTO customerResponseDTO) {
//        Link self
        Link self = linkTo(methodOn(CustomerController.class).getCustomerById(customerResponseDTO.getCustomerId())).withSelfRel();
//        Link all
        Link all = linkTo(methodOn(CustomerController.class).getAll()).withRel("All customers");
        return customerResponseDTO.add(all);
    }
}
