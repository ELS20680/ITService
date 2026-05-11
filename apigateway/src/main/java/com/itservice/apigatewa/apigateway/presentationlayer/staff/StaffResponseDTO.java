package com.itservice.apigatewa.apigateway.presentationlayer.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffResponseDTO extends RepresentationModel<StaffResponseDTO> {
    private String staffId;
    private String firstName;
    private String lastName;
    private String staffEmail;
    private StaffRole staffRole;
}
