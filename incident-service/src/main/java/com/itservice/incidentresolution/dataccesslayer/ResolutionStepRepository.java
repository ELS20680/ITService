package com.itservice.incidentresolution.dataccesslayer;

import com.itservice.incidentresolution.domain.ResolutionStep;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResolutionStepRepository extends MongoRepository<ResolutionStep, String> {
    Optional<ResolutionStep> findResolutionStepByStepIdentifier_StepId(String stepId);
    List<ResolutionStep> findByTicketIdOrderByStepNumberAsc(String ticketId);
    void deleteByTicketId(String ticketId);
}
