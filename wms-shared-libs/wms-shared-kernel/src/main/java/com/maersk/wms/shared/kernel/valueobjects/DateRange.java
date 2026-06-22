package com.maersk.wms.shared.kernel.valueobjects;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Value object representing a date/time range.
 * Used for filtering and date-based operations.
 *
 * @param startDate The start of the range (inclusive)
 * @param endDate The end of the range (inclusive)
 */
public record DateRange(Instant startDate, Instant endDate) implements Serializable {

    public DateRange {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════

    public static DateRange of(Instant start, Instant end) {
        return new DateRange(start, end);
    }

    public static DateRange of(LocalDate start, LocalDate end) {
        return new DateRange(
                start != null ? start.atStartOfDay(ZoneId.systemDefault()).toInstant() : null,
                end != null ? end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1) : null
        );
    }

    public static DateRange from(Instant start) {
        return new DateRange(start, null);
    }

    public static DateRange until(Instant end) {
        return new DateRange(null, end);
    }

    public static DateRange today() {
        LocalDate today = LocalDate.now();
        return of(today, today);
    }

    public static DateRange thisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return of(startOfWeek, endOfWeek);
    }

    public static DateRange thisMonth() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        return of(startOfMonth, endOfMonth);
    }

    public static DateRange lastNDays(int days) {
        LocalDate today = LocalDate.now();
        return of(today.minusDays(days), today);
    }

    // ═══════════════════════════════════════════════════════════════
    // OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    public boolean contains(Instant instant) {
        if (instant == null) {
            return false;
        }
        boolean afterStart = startDate == null || !instant.isBefore(startDate);
        boolean beforeEnd = endDate == null || !instant.isAfter(endDate);
        return afterStart && beforeEnd;
    }

    public boolean contains(LocalDate date) {
        return contains(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public boolean overlaps(DateRange other) {
        if (other == null) {
            return false;
        }
        boolean thisStartsBeforeOtherEnds = startDate == null || other.endDate == null ||
                !startDate.isAfter(other.endDate);
        boolean thisEndsAfterOtherStarts = endDate == null || other.startDate == null ||
                !endDate.isBefore(other.startDate);
        return thisStartsBeforeOtherEnds && thisEndsAfterOtherStarts;
    }

    public boolean isOpen() {
        return startDate == null || endDate == null;
    }

    @Override
    public String toString() {
        String start = startDate != null ? startDate.toString() : "...";
        String end = endDate != null ? endDate.toString() : "...";
        return "[" + start + " - " + end + "]";
    }
}
