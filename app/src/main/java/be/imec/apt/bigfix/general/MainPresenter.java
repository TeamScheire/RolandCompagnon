package be.imec.apt.bigfix.general;

import android.util.Log;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.notifications.TaskEventBus;
import be.imec.apt.bigfix.notifications.TaskNotificationReceiver;
import bigfix.apt.imec.be.shared.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter extends BasePresenter<MainPresenter.MainView> {
    private final CalendarDataManager calendarDataManager;
    private final TaskEventBus taskEventBus;

    private Disposable taskEventDisposable;

    private boolean isScreenVisible;

    interface MainView {
        void playNotificationSound();

        void showTaskDetail(Event event);

        void showGallery();

        void updateUser(User user);

        void openLocalizer();
    }

    @Inject
    public MainPresenter(CalendarDataManager calendarDataManager, TaskEventBus taskEventBus) {
        this.calendarDataManager = calendarDataManager;
        this.taskEventBus = taskEventBus;
    }

    public void start() {
        fetchCalendar();
        loadCurrentTask();
        updateUser(User.CHRISTINE);
    }

    public void onScreenVisible() {
        isScreenVisible = true;

        listenForTaskEvents();
    }

    public void onScreenInvisible() {
        isScreenVisible = false;

        stopListeningForTaskEvents();
    }

    public void onImecLogoClicked() {
        fetchCalendar();
    }

    public void onUserCTAClicked() {
        view.openLocalizer();
    }

    private void fetchCalendar() {
        Log.d(MainActivity.class.getSimpleName(), "Start calendar synchronization");

        final Disposable disposable = calendarDataManager.synchronizeCalendar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        calendarEvents -> Log.d(MainActivity.class.getSimpleName(), "Finished calendar synchronization"),
                        throwable -> Log.d(MainActivity.class.getSimpleName(), "Finished calendar synchronization with error: " + throwable.getMessage())
                );

        addDisposable(disposable);
    }

    private void loadCurrentTask() {
        final Disposable disposable = calendarDataManager.getCurrentTask()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::showEvent,
                        throwable -> showGallery()
                );

        addDisposable(disposable);
    }

    private void listenForTaskEvents() {
        taskEventDisposable = taskEventBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleTaskEvent);
    }

    private void updateUser(User user) {
        view.updateUser(user);
    }

    private void stopListeningForTaskEvents() {
        if (taskEventDisposable != null) {
            taskEventDisposable.dispose();
        }
    }

    private void handleTaskEvent(TaskEventBus.TaskEvent taskEvent) {
        if (taskEvent.type == TaskNotificationReceiver.Type.START || taskEvent.type == TaskNotificationReceiver.Type.SNOOZE) {
            playNotificationSound();
        }

        if (taskEvent.type == TaskNotificationReceiver.Type.START) {
            showTaskDetail(taskEvent);
        }
        if (taskEvent.type == TaskNotificationReceiver.Type.END) {
            showGallery();
        }
    }

    private void playNotificationSound() {
        view.playNotificationSound();
    }

    private void showEvent(Event event) {
        view.showTaskDetail(event);
    }

    private void showTaskDetail(TaskEventBus.TaskEvent taskEvent) {
        view.showTaskDetail(taskEvent.event);
    }

    private void showGallery() {
        view.showGallery();
    }

    private void ignoreTask(TaskEventBus.TaskEvent taskEvent) {
        final Disposable disposable = calendarDataManager.ignoreTask(taskEvent.event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        addDisposable(disposable);
    }
}
