package be.imec.apt.bigfix.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.database.entities.Event;

public class ScheduleNotificationsIntentService extends IntentService {

    @Inject
    NotificationManager notificationManager;

    @Inject
    CalendarDataManager calendarDataManager;

    public ScheduleNotificationsIntentService() {
        super("ScheduleNotificationsIntentService");

        BigFixApplication.appComponent.inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final List<Event> events = calendarDataManager.getFutureEvents(User.ROLAND, System.currentTimeMillis());
        notificationManager.scheduleNotifications(events);


    }
}
