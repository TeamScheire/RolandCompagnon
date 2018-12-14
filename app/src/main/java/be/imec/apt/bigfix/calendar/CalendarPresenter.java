package be.imec.apt.bigfix.calendar;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.di.TimeTickBus;
import bigfix.apt.imec.be.shared.BasePresenter;
import be.imec.apt.bigfix.utils.DateUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CalendarPresenter extends BasePresenter<CalendarPresenter.CalendarView> {
    private final CalendarDataManager calendarDataManager;
    private final TimeTickBus timeTickBus;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface CalendarView {

        void showCalendarOwner(User user);
        void showCalendar(List<Event> events);

        void openLocalizer();

    }
    @Inject
    public CalendarPresenter(CalendarDataManager calendarDataManager, TimeTickBus timeTickBus) {
        this.calendarDataManager = calendarDataManager;
        this.timeTickBus = timeTickBus;
    }

    public void start() {
        showCalendarOwner();

        // Wait for UI to loadCalendar, so events are measured correctly
        handler.post(this::loadCalendar);

        listenForTimeTicks();
    }

    private void showCalendarOwner() {
        view.showCalendarOwner(User.CHRISTINE);
    }

    private void showCalendar(List<Event> events) {
        view.showCalendar(events);
    }

    private void loadCalendar() {

        final long currentTime = System.currentTimeMillis();

        final long startTime = DateUtils.getTimeWithHourOffset(currentTime, -1);
        final long endTime = DateUtils.getTimeWithHourOffset(currentTime, +5);

        final Disposable disposable = calendarDataManager.getCalendar(startTime, endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showCalendar);

        disposables.add(disposable);
    }

    private void listenForTimeTicks() {
        final Disposable disposable = timeTickBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> loadCalendar());

        disposables.add(disposable);
    }

    public void onCalendarClicked() {
        view.openLocalizer();
    }
}
