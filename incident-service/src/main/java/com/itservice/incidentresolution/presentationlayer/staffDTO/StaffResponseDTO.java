package com.itservice.incidentresolution.presentationlayer.staffDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDTO {
    private String staffId;
    private String firstName;
    private String lastName;
    private String staffEmail;
    private String staffRole;
}
