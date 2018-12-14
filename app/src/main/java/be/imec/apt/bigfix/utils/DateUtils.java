package be.imec.apt.bigfix.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final SimpleDateFormat dateTimeformatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat dateformatter = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat dayformatter = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String formatDateTime(long time) {
        return dateTimeformatter.format(new Date(time));
    }

    public static String formatDate(long time) {
        return dateformatter.format(new Date(time));
    }

    public static String formatTime(long time) {
        return timeformatter.format(new Date(time));
    }

    public static String formatDay(long time) {
        return dayformatter.format(new Date(time));
    }

    public static long getStartOfDay(long datetime) {
        // We want time in milliseconds, so make sure we are in UTC timezone
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static long getStartOfDayWithOffset(long datetime, int dayOffset) {
        // We want time in milliseconds, so make sure we are in UTC timezone
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Set offset
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + dayOffset);

        return cal.getTimeInMillis();
    }

    public static int getMinutesOfHour(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.MINUTE);
    }

    public static int getHour(long time) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static long getTimeWithHourOffset(long time, int offset) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + offset);
        return cal.getTimeInMillis();
    }

    public static long getTimeWithMinuteOffset(long time, int snoozeMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + snoozeMinutes);
        return cal.getTimeInMillis();
    }
}
