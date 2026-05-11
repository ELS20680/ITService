package com.itservice.customermanagement.customermanagement.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Embeddable
@Getter
@Setter
@AllArgsConstructor
public class CustomerIdentifier {
    private String customerId;

    public CustomerIdentifier() {
        this.customerId = UUID.randomUUID().toString();
    }
}


