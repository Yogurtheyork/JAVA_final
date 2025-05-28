package UI.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDate date) {
        return date.format(DISPLAY_FORMATTER);
    }

    public static LocalDate parse(String dateStr) {
        return LocalDate.parse(dateStr, DISPLAY_FORMATTER);
    }

    public static String formatFullMonth(LocalDate date) {
        return date.getMonth().name() + " " + date.getYear();
    }
}