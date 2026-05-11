package com.itservice.incidentresolution.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "resolution_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ResolutionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private StepIdentifier stepIdentifier;

    @Column(nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private Integer stepNumber;

    @Column(nullable = false, length = 2000)
    private String actionTaken;

    @Column(nullable = false, length = 2000)
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
