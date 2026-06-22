package com.maersk.wms.shared.kernel.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Value object representing monetary amount with currency.
 * Immutable and provides arithmetic operations.
 * Used across all WMS microservices for cost and value calculations.
 *
 * @param amount The monetary amount
 * @param currency The currency code (ISO 4217)
 */
public record Money(BigDecimal amount, Currency currency) implements Serializable, Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        amount = amount.setScale(SCALE, ROUNDING);
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money usd(double amount) {
        return of(amount, "USD");
    }

    public static Money eur(double amount) {
        return of(amount, "EUR");
    }

    public static Money zero(String currencyCode) {
        return of(0, currencyCode);
    }

    // ═══════════════════════════════════════════════════════════════
    // ARITHMETIC OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(double factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public Money negate() {
        return new Money(this.amount.negate(), this.currency);
    }

    // ═══════════════════════════════════════════════════════════════
    // COMPARISON OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public int compareTo(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    // ═══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on money with different currencies: " +
                            this.currency + " vs " + other.currency);
        }
    }

    public String currencyCode() {
        return currency.getCurrencyCode();
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }
}
