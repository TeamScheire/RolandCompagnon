package be.imec.apt.bigfix.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            final Intent scheduleNotificationsIntent = new Intent(context, ScheduleNotificationsIntentService.class);
            context.startService(scheduleNotificationsIntent);
        }
    }
}
