package com.itservice.incidentresolution.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@ToString
public class StepIdentifier {
    private String stepId;

    public StepIdentifier() {
        this.stepId = UUID.randomUUID().toString();
    }
}
