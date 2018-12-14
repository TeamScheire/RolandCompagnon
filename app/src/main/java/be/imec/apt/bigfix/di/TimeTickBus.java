package be.imec.apt.bigfix.di;

import io.reactivex.subjects.PublishSubject;

public class TimeTickBus {
    private final PublishSubject<Long> bus = PublishSubject.create();

    public void send(Long currentTime) {
        bus.onNext(currentTime);
    }

    public PublishSubject<Long> toObservable() {
        return bus;
    }
}
