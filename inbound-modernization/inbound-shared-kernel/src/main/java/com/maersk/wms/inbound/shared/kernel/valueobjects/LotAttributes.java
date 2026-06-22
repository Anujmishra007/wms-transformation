package com.maersk.wms.inbound.shared.kernel.valueobjects;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Value object representing lot attributes for inventory tracking.
 * Immutable and used across all inbound subdomains.
 *
 * Part of Shared Kernel - can be used by all bounded contexts.
 */
public final class LotAttributes {

    private final String lot;
    private final LocalDate manufactureDate;
    private final LocalDate expirationDate;
    private final String serialNumber;
    private final String batchNumber;
    private final String countryOfOrigin;
    private final Map<String, String> lottables;

    private LotAttributes(Builder builder) {
        this.lot = builder.lot;
        this.manufactureDate = builder.manufactureDate;
        this.expirationDate = builder.expirationDate;
        this.serialNumber = builder.serialNumber;
        this.batchNumber = builder.batchNumber;
        this.countryOfOrigin = builder.countryOfOrigin;
        this.lottables = Map.copyOf(builder.lottables);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLot() {
        return lot;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public String getLottable(String key) {
        return lottables.get(key);
    }

    public Map<String, String> getLottables() {
        return lottables;
    }

    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(expirationDate);
    }

    public long getDaysUntilExpiration() {
        if (expirationDate == null) {
            return Long.MAX_VALUE;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
    }

    public boolean hasMinimumShelfLife(int minDays) {
        return getDaysUntilExpiration() >= minDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LotAttributes that = (LotAttributes) o;
        return Objects.equals(lot, that.lot) &&
               Objects.equals(manufactureDate, that.manufactureDate) &&
               Objects.equals(expirationDate, that.expirationDate) &&
               Objects.equals(serialNumber, that.serialNumber) &&
               Objects.equals(batchNumber, that.batchNumber) &&
               Objects.equals(countryOfOrigin, that.countryOfOrigin) &&
               Objects.equals(lottables, that.lottables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lot, manufactureDate, expirationDate, serialNumber, batchNumber, countryOfOrigin, lottables);
    }

    @Override
    public String toString() {
        return "LotAttributes{lot='" + lot + "', expirationDate=" + expirationDate + "}";
    }

    public static class Builder {
        private String lot;
        private LocalDate manufactureDate;
        private LocalDate expirationDate;
        private String serialNumber;
        private String batchNumber;
        private String countryOfOrigin;
        private Map<String, String> lottables = new HashMap<>();

        public Builder lot(String lot) {
            this.lot = lot;
            return this;
        }

        public Builder manufactureDate(LocalDate manufactureDate) {
            this.manufactureDate = manufactureDate;
            return this;
        }

        public Builder expirationDate(LocalDate expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public Builder batchNumber(String batchNumber) {
            this.batchNumber = batchNumber;
            return this;
        }

        public Builder countryOfOrigin(String countryOfOrigin) {
            this.countryOfOrigin = countryOfOrigin;
            return this;
        }

        public Builder lottable(String key, String value) {
            this.lottables.put(key, value);
            return this;
        }

        public Builder lottables(Map<String, String> lottables) {
            this.lottables.putAll(lottables);
            return this;
        }

        public LotAttributes build() {
            return new LotAttributes(this);
        }
    }
}
