package be.imec.apt.bigfix.info;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.database.entities.Event;
import bigfix.apt.imec.be.shared.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalizerPresenter extends BasePresenter<LocalizerPresenter.LocalizerView> {

    private final CalendarDataManager calendarDataManager;

    public interface LocalizerView {
        void showEvent(Event event);

        void showNoEvents();

        void showUser(User user);
    }

    @Inject
    public LocalizerPresenter(CalendarDataManager calendarDataManager) {
        this.calendarDataManager = calendarDataManager;
    }

    public void start() {
        showUser();
        loadCurrentEvent();
    }

    private void loadCurrentEvent() {
        final Disposable disposable = calendarDataManager.getCurrentEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showEvent,
                        throwable -> showNoActivities()
                );

        addDisposable(disposable);
    }

    private void showUser() {
        view.showUser(User.CHRISTINE);
    }

    private void showEvent(Event event) {
        view.showEvent(event);
    }

    private void showNoActivities() {
        view.showNoEvents();
    }
}
