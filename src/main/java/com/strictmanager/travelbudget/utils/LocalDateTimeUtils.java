package com.strictmanager.travelbudget.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

    public static LocalDateTime atStartOfDay(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static LocalDateTime atMaxOfDay(LocalDate localDate) {
        return localDate.atTime(23, 59, 59);
    }
}
