package com.itservice.incidentresolution.presentationlayer.customerDTO;

import lombok.Data;

@Data
public class CustomerRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String department;
}
