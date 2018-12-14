package be.imec.apt.bigfix.tasks.overview;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.di.DateChangeBus;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.notifications.TaskEventBus;
import be.imec.apt.bigfix.notifications.TaskNotificationReceiver;
import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.utils.DateUtils;
import bigfix.apt.imec.be.shared.BasePresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class TasksPresenter extends BasePresenter<TasksPresenter.TasksView> {

    private final CalendarDataManager calendarDataManager;
    private final TaskEventBus taskEventBus;
    private final DateChangeBus dateChangeBus;

    private List<Task> tasks;

    private Disposable tasksDisposable;
    private Disposable taskEventDisposable;

    interface TasksView {
        void updateUser(User user);

        void showTasks(List<Task> tasks);

        void updateTask(int position);

        void startTask(Task task);
    }

    @Inject
    TasksPresenter(CalendarDataManager calendarDataManager, TaskEventBus taskEventBus, DateChangeBus dateChangeBus) {
        this.calendarDataManager = calendarDataManager;
        this.taskEventBus = taskEventBus;
        this.dateChangeBus = dateChangeBus;
    }

    public void start() {
        updateUser(User.ROLAND);

        listenForDateChanges();
        loadTasks();
    }

    public void onScreenVisible() {
        listenForTaskEvents();
    }

    public void onScreenInvisible() {
        if (taskEventDisposable != null) {
            taskEventDisposable.dispose();
        }
    }

    private void updateUser(User user) {
        view.updateUser(user);
    }

    private void listenForDateChanges() {
        final Disposable disposable = dateChangeBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> loadTasks());

        addDisposable(disposable);
    }

    private void loadTasks() {
        if (tasksDisposable != null && !tasksDisposable.isDisposed()) {
            tasksDisposable.dispose();
        }

        final long day = DateUtils.getStartOfDay(System.currentTimeMillis());
        tasksDisposable = calendarDataManager.getTasks(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showTasks, this::showError);

        addDisposable(tasksDisposable);
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
        view.showTasks(new ArrayList<>());
    }

    private void showTasks(List<Task> tasks) {
        this.tasks = tasks;

        view.showTasks(tasks);
    }

    private void listenForTaskEvents() {
        taskEventDisposable = taskEventBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateTask);
    }

    private void updateTask(TaskEventBus.TaskEvent taskEvent) {
        if (this.tasks == null || taskEvent.type == TaskNotificationReceiver.Type.SNOOZE) {
            return;
        }

        //noinspection SuspiciousMethodCalls
        final int position = tasks.indexOf(taskEvent.event);
        view.updateTask(position);
    }

    public void onTaskClicked(Task task) {
        if (task.getEvent().isCompleted()) {
            return;
        }

        final Disposable disposable = calendarDataManager.startTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        addDisposable(disposable);

        view.startTask(task);
    }
}
