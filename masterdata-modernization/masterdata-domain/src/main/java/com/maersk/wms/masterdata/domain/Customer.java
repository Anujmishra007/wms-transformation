package com.maersk.wms.masterdata.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Customer master data entity.
 * Maps to the STORER table in the WMS database (for owners/clients)
 * and SHIPTOADDRESS for ship-to customers.
 */
@Data
public class Customer {

    private Long id;
    private String customerCode;
    private String customerName;
    private CustomerType customerType;
    private CustomerStatus status;

    // Contact information
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String contactFax;

    // Primary address
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Shipping defaults
    private String defaultCarrier;
    private String defaultShipMethod;
    private String defaultWarehouse;
    private String shippingInstructions;

    // Billing
    private String billingAddress1;
    private String billingAddress2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String billingCountry;
    private String taxId;
    private String vatNumber;

    // EDI / Integration
    private String ediCustomerId;
    private String externalCustomerId;
    private boolean ediEnabled;
    private String asnFormat;
    private String invoiceFormat;

    // Compliance
    private boolean requiresRoutingGuide;
    private boolean requiresUccLabels;
    private boolean requiresAsn;
    private String complianceLabelFormat;
    private String packingSlipFormat;

    // Priority and service level
    private int priority;
    private String serviceLevel;
    private boolean vipCustomer;

    // Custom fields
    private String customField01;
    private String customField02;
    private String customField03;

    // Audit
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
