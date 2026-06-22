package com.maersk.wms.masterdata.shared.kernel.valueobjects;

/**
 * Value object representing contact information.
 */
public record ContactInfo(
        String contactName,
        String email,
        String phone,
        String fax,
        String mobile
) {
    public static ContactInfo empty() {
        return new ContactInfo(null, null, null, null, null);
    }

    public static ContactInfo of(String contactName, String email, String phone) {
        return new ContactInfo(contactName, email, phone, null, null);
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }
}
