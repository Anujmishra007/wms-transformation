package com.maersk.wms.inbound.rules;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of putaway location determination rules.
 */
@Data
public class PutawayLocationResult {

    private String selectedLocation;
    private String selectedZone;
    private String putawayStrategy;
    private int priority;
    private boolean requiresConsolidation;
    private boolean requiresInspectionFirst;
    private String reasonCode;
    private List<String> candidateLocations = new ArrayList<>();
    private List<String> messages = new ArrayList<>();

    public void addCandidateLocation(String location) {
        candidateLocations.add(location);
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
