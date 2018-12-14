package be.imec.apt.bigfix.general;

import android.support.annotation.DrawableRes;

import be.imec.apt.bigfix.BuildConfig;
import be.imec.apt.bigfix.R;

public enum User {
    CHRISTINE("Christine", R.drawable.christine, BuildConfig.ICAL_CALENDAR_GROUP_ID, BuildConfig.ICAL_CALENDAR_USER_ID),
    ROLAND("Roland", BuildConfig.ICAL_TASKS_GROUP_ID, BuildConfig.ICAL_TASKS_USER_ID);

    private final String name;
    @DrawableRes
    private int avatar;
    private final String calendarGroupId;
    private final String calendarUserId;

    User(String name, @DrawableRes int avatar, String calendarGroupId, String calendarUserId) {
        this(name, calendarGroupId, calendarUserId);
        this.avatar = avatar;
    }

    User(String name, String calendarGroupId, String calendarUserId) {
        this.name = name;
        this.calendarGroupId = calendarGroupId;
        this.calendarUserId = calendarUserId;
    }

    public String getId() {
        return name;
    }

    public String getName() {
        return name;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getCalendarGroupId() {
        return calendarGroupId;
    }

    public String getCalendarUserId() {
        return calendarUserId;
    }
}
