package com.maersk.wms.outbound.domain.shipping.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Result DTO for address validation operations.
 */
@Data
@Builder
public class AddressValidationResult {
    private boolean valid;
    private boolean corrected;
    private String correctedAddress1;
    private String correctedAddress2;
    private String correctedCity;
    private String correctedState;
    private String correctedPostalCode;
    private String correctedCountry;
    private List<String> errors;
    private List<String> warnings;
    private String errorCode;
}
