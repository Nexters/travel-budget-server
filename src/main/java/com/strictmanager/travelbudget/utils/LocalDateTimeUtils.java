package com.strictmanager.travelbudget.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public final class LocalDateTimeUtils {
    private LocalDateTimeUtils() {}

    public static Long convertToTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime convertToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(timestamp),
            ZoneId.systemDefault()
        );
    }

    public static LocalDateTime atUtcStartOfDay(LocalDate localDate) {
        return localDate.atStartOfDay().plus(-9, ChronoUnit.HOURS);
    }

    public static LocalDateTime atUtcMaxOfDay(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX).plus(-9, ChronoUnit.HOURS);
    }
}
