package com.maersk.wms.masterdata.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Temporal activity interface for customer operations.
 */
@ActivityInterface
public interface CustomerActivities {

    @ActivityMethod
    CustomerResult createCustomer(Map<String, Object> customerData, String clientCode, String facilityCode);

    @ActivityMethod
    CustomerResult updateCustomer(String customerCode, Map<String, Object> customerData, String clientCode, String facilityCode);

    @ActivityMethod
    CustomerResult validateCustomer(Map<String, Object> customerData, String clientCode, String facilityCode);

    @ActivityMethod
    AddressValidationResult validateAddress(String address1, String address2, String city, String state, String postalCode, String country);

    @Data
    @Builder
    class CustomerResult {
        private boolean success;
        private String customerCode;
        private String errorMessage;
        private String errorCode;
        private Map<String, Object> customerData;
    }

    @Data
    @Builder
    class AddressValidationResult {
        private boolean valid;
        private String standardizedAddress1;
        private String standardizedAddress2;
        private String standardizedCity;
        private String standardizedState;
        private String standardizedPostalCode;
        private String errorMessage;
    }
}
