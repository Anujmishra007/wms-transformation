package com.maersk.wms.masterdata.shared.kernel.valueobjects;

/**
 * Value object representing a physical address.
 */
public record Address(
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String countryCode
) {
    public Address {
        if (country != null && countryCode == null) {
            countryCode = country.length() <= 3 ? country : country.substring(0, 2).toUpperCase();
        }
    }

    public static Address empty() {
        return new Address(null, null, null, null, null, null, null);
    }

    public String fullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null) sb.append(addressLine1);
        if (addressLine2 != null) sb.append(", ").append(addressLine2);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (postalCode != null) sb.append(" ").append(postalCode);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }

    public boolean isComplete() {
        return addressLine1 != null && city != null && country != null;
    }
}
