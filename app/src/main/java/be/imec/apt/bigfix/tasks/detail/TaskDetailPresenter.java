package be.imec.apt.bigfix.tasks.detail;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import bigfix.apt.imec.be.shared.BasePresenter;
import be.imec.apt.bigfix.tasks.Task;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class TaskDetailPresenter extends BasePresenter<TaskDetailPresenter.TaskDetailView> {

    interface TaskDetailView {
        void showTask(Task task);

        void showTaskStarted(Task currentTask);
    }

    private final CalendarDataManager calendarDataManager;

    private Task currentTask;

    @Inject
    public TaskDetailPresenter(CalendarDataManager calendarDataManager) {
        this.calendarDataManager = calendarDataManager;
    }

    public void start(long eventId) {
        final Disposable disposable = calendarDataManager.getEvent(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTask);

        addDisposable(disposable);
    }

    public void onStartTaskClicked() {
        final Disposable disposable = calendarDataManager.startTask(currentTask)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTaskStarted);

        addDisposable(disposable);
    }

    private void showTask(Task task) {
        this.currentTask = task;

        view.showTask(task);
    }

    private void showTaskStarted() {
        view.showTaskStarted(currentTask);
    }
}
