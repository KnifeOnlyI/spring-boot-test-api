package fr.koi.testapi.util;

import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Contains all date formatters
 */
public final class DateFormatter {

    /**
     * Hidden constructor
     */
    private DateFormatter() {
    }

    /**
     * Convert the specified local date time to string (format : 2022-01-01T23:59:59)
     *
     * @param date The date to convert
     *
     * @return The corresponding string
     */
    @String
    @SuppressWarnings("java:S109")
    public static java.lang.String map(final java.time.LocalDateTime date) {
        return date.toString().substring(0, 19);
    }

    /**
     * Convert the specified string to local date time (format : 2022-01-01T23:59:59[.317676500])
     *
     * @param date The date to convert
     *
     * @return The corresponding string
     */
    @LocalDateTime
    public static java.time.LocalDateTime map(final java.lang.String date) {
        return java.time.LocalDateTime.parse(date);
    }

    /**
     * The annotation for mapping to LocalDateTime
     */
    @Qualifier
    @Target(ElementType.METHOD)
    public @interface LocalDateTime {

    }

    /**
     * The annotation for mapping to String
     */
    @Qualifier
    @Target(ElementType.METHOD)
    public @interface String {

    }
}
