package com.itservice.staffmanagement.staffmanagement.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private StaffIdentifier staffIdentifier;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;
}