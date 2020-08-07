package com.strictmanager.travelbudget.utils;

import java.time.Instant;
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
}
