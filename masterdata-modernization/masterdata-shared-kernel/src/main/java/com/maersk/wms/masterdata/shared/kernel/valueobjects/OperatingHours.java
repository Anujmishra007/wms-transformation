package com.maersk.wms.masterdata.shared.kernel.valueobjects;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

/**
 * Value object representing operating hours for a facility or dock.
 */
public record OperatingHours(
        LocalTime openTime,
        LocalTime closeTime,
        Set<DayOfWeek> operatingDays,
        Map<DayOfWeek, LocalTime> customOpenTimes,
        Map<DayOfWeek, LocalTime> customCloseTimes,
        String timezone
) {
    public static OperatingHours twentyFourSeven() {
        return new OperatingHours(
                LocalTime.of(0, 0),
                LocalTime.of(23, 59),
                Set.of(DayOfWeek.values()),
                Map.of(), Map.of(), "UTC"
        );
    }

    public static OperatingHours standard() {
        return new OperatingHours(
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                       DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                Map.of(), Map.of(), "UTC"
        );
    }

    public boolean isOperatingDay(DayOfWeek day) {
        return operatingDays.contains(day);
    }

    public LocalTime getOpenTime(DayOfWeek day) {
        return customOpenTimes.getOrDefault(day, openTime);
    }

    public LocalTime getCloseTime(DayOfWeek day) {
        return customCloseTimes.getOrDefault(day, closeTime);
    }
}
