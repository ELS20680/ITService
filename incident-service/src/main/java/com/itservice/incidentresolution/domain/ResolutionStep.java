package com.itservice.incidentresolution.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "resolution_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResolutionStep {

    @Id
    private String id;

    private StepIdentifier stepIdentifier;

    private String ticketId;

    private Integer stepNumber;

    private String actionTaken;

    private String result;

    private LocalDateTime createdAt;
}
