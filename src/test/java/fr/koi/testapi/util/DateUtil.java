package fr.koi.testapi.util;

import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

/**
 * Contains all utilitaries about dates
 */
public final class DateUtil {
    /**
     * Compare the two specified date and assert are equals
     *
     * @param a The first date
     * @param b The second date
     */
    public static void assertEquals(LocalDateTime a, LocalDateTime b) {
        Assertions.assertEquals(a.getYear(), b.getYear());
        Assertions.assertEquals(a.getMonth(), b.getMonth());
        Assertions.assertEquals(a.getDayOfMonth(), b.getDayOfMonth());
        Assertions.assertEquals(a.getHour(), b.getHour());
        Assertions.assertEquals(a.getMinute(), b.getMinute());
        Assertions.assertEquals(a.getSecond(), b.getSecond());
    }

    /**
     * Compare the two specified date and assert are equals
     *
     * @param a The first date
     * @param b The second date
     */
    public static void assertEquals(LocalDateTime a, String b) {
        DateUtil.assertEquals(a, DateFormatter.map(b));
    }
}
