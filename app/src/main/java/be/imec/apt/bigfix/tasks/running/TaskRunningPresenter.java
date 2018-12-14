package be.imec.apt.bigfix.tasks.running;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import bigfix.apt.imec.be.shared.BasePresenter;
import be.imec.apt.bigfix.notifications.TaskEventBus;
import be.imec.apt.bigfix.tasks.Task;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import bigfix.apt.imec.be.shared.TaskStatusEventBus;
import bigfix.apt.imec.be.shared.WearableNotConnectedException;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskRunningPresenter extends BasePresenter<TaskRunningPresenter.TaskRunningView> {

    private final CalendarDataManager calendarDataManager;
    private final TaskEventBus taskEventBus;
    private final TaskStatusEventBus taskStatusEventBus;
    private final WearableManager wearableManager;

    private Task task;

    public interface TaskRunningView {
        void showTask(Task task);

        void closeScreen();

        void showCancelConfirmationDialog();

        void showWearableNotConnectedError();
    }

    @Inject
    public TaskRunningPresenter(CalendarDataManager calendarDataManager, TaskEventBus taskEventBus, TaskStatusEventBus taskStatusEventBus, WearableManager wearableManager) {
        this.calendarDataManager = calendarDataManager;
        this.taskEventBus = taskEventBus;
        this.taskStatusEventBus = taskStatusEventBus;
        this.wearableManager = wearableManager;
    }

    public void start(Task task) {
        this.task = task;

        showTask(task);
        listenForTaskEvents();
        listenForTaskStatusEvents();
        startTaskOnWearable(task);
    }

    private void startTaskOnWearable(Task task) {
        final Disposable disposable = wearableManager.startTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                }, this::handleTaskStartError);

        addDisposable(disposable);
    }

    private void handleTaskStartError(Throwable throwable) {
        if (!(throwable instanceof WearableNotConnectedException)) {
            Crashlytics.logException(throwable);
        }

        view.showWearableNotConnectedError();
    }

    private void showTask(Task task) {
        view.showTask(task);
    }

    private void listenForTaskEvents() {
        final Disposable disposable = taskEventBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onOtherTaskReceived);

        addDisposable(disposable);
    }

    private void onOtherTaskReceived(TaskEventBus.TaskEvent taskEvent) {
        final Disposable disposable = calendarDataManager.ignoreTask(taskEvent.event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        addDisposable(disposable);
    }

    private void listenForTaskStatusEvents() {
        final Disposable disposable = taskStatusEventBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskStatusReceived);

        addDisposable(disposable);
    }

    private void onTaskStatusReceived(TaskStatusEvent taskStatusEvent) {
        if (task.getEvent().getId() != taskStatusEvent.getTaskId()) {
            return;
        }

        if (taskStatusEvent.isCompleted()) {
            completeTask(task);
        } else if (taskStatusEvent.isCanceled()) {
            closeScreen();
        }
    }

    private void completeTask(Task task) {
        final Disposable disposable = calendarDataManager.completeTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::closeScreen);

        addDisposable(disposable);
    }

    @SuppressWarnings("EmptyMethod")
    public void onBackPressed() {
        // Do nothing
    }

    public void onTaskStartErrorTryAgain() {
        startTaskOnWearable(task);
    }

    public void onTaskStartErrorCanceled() {
        final Disposable disposable = wearableManager.cancelTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::closeScreen);

        addDisposable(disposable);
    }

    public void onTaskCancelClicked() {
        view.showCancelConfirmationDialog();
    }

    public void onTaskCancelConfirmationClicked() {
        final Disposable disposable = calendarDataManager.stopTask(task)
                .andThen(wearableManager.cancelTask(task))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::closeScreen);

        addDisposable(disposable);
    }

    public void onTaskCancelConfirmationCanceled() {
        // Do nothing
    }

    private void closeScreen() {
        view.closeScreen();
    }
}
