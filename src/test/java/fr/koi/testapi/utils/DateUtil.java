package fr.koi.testapi.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utilitary class to manage dates
 */
public final class DateUtil {
    /**
     * Convert a string formated date for database to a java 8 UTC Date
     * format : yyyy-MM-dd HH:mm:ss
     * e.g : 2021-12-31 23:59:59
     *
     * @param date The date string to convert
     *
     * @return The corresponding java 8 Date
     */
    public static Date fromDatabaseString(String date) {
        // Get localdate from the 19 first characters
        LocalDateTime l = LocalDateTime.parse(date.substring(0, 19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Convert to system timezone
        Instant i = l.toInstant(OffsetDateTime.now().getOffset());

        return Date.from(i);
    }
}
