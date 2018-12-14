package be.imec.apt.bigfix.notifications;

import android.os.Looper;
import android.util.Log;

import javax.inject.Inject;

import be.imec.apt.bigfix.database.AppDatabase;
import be.imec.apt.bigfix.database.entities.Event;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class TaskEventBus {
    private final PublishSubject<TaskEvent> bus = PublishSubject.create();

    private final AppDatabase database;

    @Inject
    public TaskEventBus(AppDatabase database) {
        this.database = database;
    }

    public void send(TaskEvent event) {
        bus.onNext(event);
    }

    public Observable<TaskEvent> toObservable() {
        return bus
                .observeOn(Schedulers.io())
                .map(taskEvent -> {
                    Log.d("DATABASE", "Querying on " + Looper.myLooper());
                    taskEvent.event = database.calendarDao().getEvent(taskEvent.eventId);

                    return taskEvent;
                });
    }

    public static class TaskEvent {
        public final long eventId;
        public Event event;
        public final TaskNotificationReceiver.Type type;

        TaskEvent(long eventId, TaskNotificationReceiver.Type type) {
            this.eventId = eventId;
            this.type = type;
        }
    }
}
