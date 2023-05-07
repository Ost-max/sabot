package dev.ostmax.sabot.service.time;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateTimeUtils {


    public static final Locale locale = Locale.of("RU");
    public static final DateTimeFormatter simple_date_month = DateTimeFormatter.ofPattern("d MMMM").withLocale(Locale.of("RU"));

    public static final DateTimeFormatter simple_date_time = DateTimeFormatter.ofPattern("d MMMM, hh:mm").withLocale(Locale.of("RU"));

    public static String getFormattedMonthName(LocalDate date) {
       return StringUtils.capitalize(date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, locale));
    }

    public static String formatDateTime(LocalDateTime date) {
        return date.format(simple_date_time);
    }
}
