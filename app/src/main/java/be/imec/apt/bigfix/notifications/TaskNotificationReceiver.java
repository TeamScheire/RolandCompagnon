package be.imec.apt.bigfix.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BigFixApplication;

public class TaskNotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_EVENT_ID = "extraEventId";
    public static final String EXTRA_TYPE = "extraStart";

    public enum Type {
        START,
        SNOOZE,
        END
    }

    @Inject
    TaskEventBus taskEventBus;

    public TaskNotificationReceiver() {
        BigFixApplication.appComponent.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() == null || !intent.hasExtra(EXTRA_EVENT_ID) || !intent.hasExtra(EXTRA_TYPE)) {
            return;
        }

        final long eventId = intent.getExtras().getLong(EXTRA_EVENT_ID);
        final Type type = Type.values()[intent.getExtras().getInt(EXTRA_TYPE)];
        taskEventBus.send(new TaskEventBus.TaskEvent(eventId, type));
    }
}
