package bigfix.apt.imec.be.shared;

import io.reactivex.subjects.PublishSubject;

public class TaskStatusEventBus {
    private final PublishSubject<TaskStatusEvent> bus = PublishSubject.create();

    public TaskStatusEventBus() {

    }

    public void send(TaskStatusEvent event) {
        bus.onNext(event);
    }

    public PublishSubject<TaskStatusEvent> toObservable() {
        return bus;
    }
}
