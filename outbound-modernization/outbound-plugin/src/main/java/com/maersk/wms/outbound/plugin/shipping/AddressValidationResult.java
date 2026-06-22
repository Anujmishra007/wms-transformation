package com.maersk.wms.outbound.plugin.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of carrier address validation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationResult {

    private boolean valid;
    private boolean residential;

    // Standardized address from carrier
    private String standardizedAddress1;
    private String standardizedAddress2;
    private String standardizedCity;
    private String standardizedState;
    private String standardizedZip;
    private String standardizedZipPlus4;
    private String standardizedCountry;

    // Validation messages
    @Builder.Default
    private List<String> messages = new ArrayList<>();

    // Suggested alternatives if address is invalid
    @Builder.Default
    private List<SuggestedAddress> suggestions = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedAddress {
        private String address1;
        private String address2;
        private String city;
        private String state;
        private String zip;
        private String country;
        private double matchScore;
    }
}
