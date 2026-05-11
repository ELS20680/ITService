package com.itservice.staffmanagement.staffmanagement.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class StaffIdentifier {
    private String staffId;

    public StaffIdentifier() {
        this.staffId = UUID.randomUUID().toString();
    }

}
