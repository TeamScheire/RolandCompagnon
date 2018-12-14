package be.imec.apt.bigfix.di;

import javax.inject.Singleton;

import be.imec.apt.bigfix.calendar.CalendarFragment;
import be.imec.apt.bigfix.notifications.ScheduleNotificationsIntentService;
import be.imec.apt.bigfix.tasks.running.MessageReceiverService;
import be.imec.apt.bigfix.utils.TimeTickBroadcastReceiver;
import be.imec.apt.bigfix.general.MainActivity;
import be.imec.apt.bigfix.info.InfoFragment;
import be.imec.apt.bigfix.info.LocalizerDialogFragment;
import be.imec.apt.bigfix.jobscheduler.CalendarSyncJob;
import be.imec.apt.bigfix.notifications.TaskNotificationReceiver;
import be.imec.apt.bigfix.tasks.detail.TaskDetailFragment;
import be.imec.apt.bigfix.tasks.running.TaskRunningActivity;
import be.imec.apt.bigfix.tasks.overview.TasksFragment;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, PersistenceModule.class, NetworkModule.class, EventBusModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(CalendarSyncJob calendarSyncJob);

    void inject(TimeTickBroadcastReceiver timeTickBroadcastReceiver);

    void inject(CalendarFragment calendarFragment);

    void inject(InfoFragment infoFragment);

    void inject(LocalizerDialogFragment localizerDialogFragment);

    void inject(TasksFragment tasksFragment);

    void inject(TaskNotificationReceiver taskNotificationReceiver);

    void inject(TaskDetailFragment taskDetailFragment);

    void inject(TaskRunningActivity taskRunningActivity);

    void inject(ScheduleNotificationsIntentService scheduleNotificationsIntentService);

    void inject(MessageReceiverService messageReceiverService);
}