package be.imec.apt.bigfix.calendar;

final class CalendarUtils {
    private static final int HOURS_TO_SHOW = 5;
    private static final int SECOND_IN_MILLISECONDS = 1000;
    private static final double SECONDS_IN_MINUTE = 60.0;

    private CalendarUtils() {

    }

    public static int calculateHourBlock(int parentHeight) {
        return parentHeight / HOURS_TO_SHOW;
    }

    public static int getFirstHourOffset(int blockHeight, int currentMinutes) {
        return (int) (getMinuteHeight(blockHeight) * (SECONDS_IN_MINUTE - currentMinutes));
    }

    public static double getMinuteHeight(int blockHeight) {
        return blockHeight / SECONDS_IN_MINUTE;
    }

    public static int getFirstHourToShow(int currentHour, int currentMinutes) {
        return currentMinutes == 0 ? currentHour - 1 : currentHour;
    }

    public static int getDurationInMinutes(long startHour, long endHour) {
        return (int) ((endHour - startHour) / SECOND_IN_MILLISECONDS / SECONDS_IN_MINUTE);
    }

    public static int getHoursToShow() {
        return HOURS_TO_SHOW;
    }
}
