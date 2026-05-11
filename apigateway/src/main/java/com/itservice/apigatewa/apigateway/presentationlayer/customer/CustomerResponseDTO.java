package com.itservice.apigatewa.apigateway.presentationlayer.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDTO extends RepresentationModel<CustomerResponseDTO> {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
}