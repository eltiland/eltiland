package com.eltiland.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Common date utils.
 *
 * @author Aleksey Plotnikov
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private static SimpleDateFormat dateFullFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
    public static SimpleDateFormat dateDBFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateRussianFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));

    /**
     * @param date date to format
     * @return String representation of given date in format "dd.MM.yy")
     */
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * @param date date to format
     * @return String representation of given date in format "dd.MM.yy HH:mm")
     */
    public static String formatFullDate(Date date) {
        return dateFullFormat.format(date);
    }

    /**
     * @param date date to format
     * @return String representation of given date in russian format
     */
    public static String formatRussianDate(Date date) {
        return dateRussianFormat.format(date);
    }

    /**
     * @param date date to format
     * @return String representation of given date in DB format
     */
    public static String formatDBDate(Date date) {
        return dateDBFormat.format(date);
    }

    public static Date getCurrentDate() {
        DateHelper helper = new DateHelper();
        DateTime.now().plusHours(helper.getShift());
        return DateTime.now().plusHours(helper.getShift()).toDate();
    }
}
