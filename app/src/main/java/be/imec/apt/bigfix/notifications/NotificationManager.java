package be.imec.apt.bigfix.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.utils.DateUtils;

public class NotificationManager {

    private static final int SNOOZE_MINUTES = 2;

    private final Context context;
    private final AlarmManager alarmManager;

    @Inject
    public NotificationManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNotifications(List<Event> events) {
        for (Event event : events) {
            scheduleNotifications(event);
        }
    }

    public void scheduleNotifications(Event event) {
        scheduleStartNotification(event);
        scheduleSnoozeNotification(event);
        scheduleEndNotification(event);
    }

    public void unscheduleNotifications(List<Event> events) {
        for (Event event : events) {
            unscheduleNotifications(event);
        }
    }

    public void unscheduleNotifications(Event event) {
        unscheduleStartNotification(event);
        unscheduleSnoozeNotifications(event);
        unscheduleEndNotification(event);
    }

    private void addNotification(Event event, long time, TaskNotificationReceiver.Type type) {
        Log.d("SCHEDULER", event.getSummary() + ": " + type.name() + ": " + DateUtils.formatDateTime(time));

        final PendingIntent pendingIntent = createPendingIntent(event, time, type);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private void removeNotification(Event event, long time, TaskNotificationReceiver.Type type) {
        final PendingIntent pendingIntent = createPendingIntent(event, time, type);
        alarmManager.cancel(pendingIntent);
    }

    private PendingIntent createPendingIntent(Event event, long time, TaskNotificationReceiver.Type type) {
        final Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra(TaskNotificationReceiver.EXTRA_EVENT_ID, event.getId());
        intent.putExtra(TaskNotificationReceiver.EXTRA_TYPE, type.ordinal());

        return PendingIntent.getBroadcast(context, Objects.hash(event, time), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void scheduleStartNotification(Event event) {
        if (event.getStartTime() < System.currentTimeMillis()) {
            return;
        }

        addNotification(event, event.getStartTime(), TaskNotificationReceiver.Type.START);
    }

    private void unscheduleStartNotification(Event event) {
        removeNotification(event, event.getStartTime(), TaskNotificationReceiver.Type.START);
    }

    private void scheduleSnoozeNotification(Event event) {
        long snooze = DateUtils.getTimeWithMinuteOffset(event.getStartTime(), SNOOZE_MINUTES);
        //TODO add check so snooze is only added when event hasn't been started yet
        while (snooze < event.getEndTime()) {
            if (snooze > System.currentTimeMillis()) {
                addNotification(event, snooze, TaskNotificationReceiver.Type.SNOOZE);
            }

            snooze = DateUtils.getTimeWithMinuteOffset(snooze, SNOOZE_MINUTES);
        }
    }

    private void unscheduleSnoozeNotifications(Event event) {
        long snooze = DateUtils.getTimeWithMinuteOffset(event.getStartTime(), SNOOZE_MINUTES);
        while (snooze < event.getEndTime()) {
            removeNotification(event, snooze, TaskNotificationReceiver.Type.SNOOZE);

            snooze = DateUtils.getTimeWithMinuteOffset(snooze, SNOOZE_MINUTES);
        }
    }

    private void scheduleEndNotification(Event event) {
        if (event.getEndTime() < System.currentTimeMillis()) {
            return;
        }

        addNotification(event, event.getEndTime(), TaskNotificationReceiver.Type.END);
    }

    private void unscheduleEndNotification(Event event) {
        removeNotification(event, event.getEndTime(), TaskNotificationReceiver.Type.END);
    }
}
