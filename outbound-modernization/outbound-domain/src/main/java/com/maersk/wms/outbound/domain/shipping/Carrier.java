package com.maersk.wms.outbound.domain.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrier entity representing a shipping carrier.
 * Maps to STORER table (StorerType=3) in the legacy system.
 *
 * Legacy SP References:
 * - isp_ConnectCarrierService
 * - isp_UpdateCarrierService
 * - isp_ValidateCarrierChange
 * - isp_Carrier_Middleware_Interface
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrier {

    private String carrierKey;
    private String carrierCode;
    private String carrierName;
    private String carrierType;

    private String scac;  // Standard Carrier Alpha Code
    private String dotNumber;
    private String mcNumber;

    private String contactName;
    private String contactPhone;
    private String contactEmail;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;

    private CarrierStatus status;

    // API Integration settings
    private String apiEndpoint;
    private String apiKey;
    private String accountNumber;
    private String meterNumber;

    // Supported services
    @Builder.Default
    private List<CarrierService> services = new ArrayList<>();

    private String addWho;
    private LocalDateTime addDate;
    private String editWho;
    private LocalDateTime editDate;

    /**
     * Check if carrier is active and can be used for shipping.
     */
    public boolean isActive() {
        return status == CarrierStatus.ACTIVE;
    }

    /**
     * Check if carrier supports a specific service.
     */
    public boolean supportsService(String serviceCode) {
        return services.stream()
                .anyMatch(s -> s.getServiceCode().equals(serviceCode) && s.isActive());
    }

    /**
     * Get name (alias for carrierName).
     */
    public String getName() {
        return carrierName;
    }
}
