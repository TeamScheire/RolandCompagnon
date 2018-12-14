package be.imec.apt.bigfix.wear;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import bigfix.apt.imec.be.shared.BasePresenter;
import bigfix.apt.imec.be.shared.TaskInstructions;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import bigfix.apt.imec.be.shared.TaskStatusEventBus;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TaskInstructionPresenter extends BasePresenter<TaskInstructionPresenter.TaskInstructionView> {
    private static final int REPEAT_INSTRUCTION_INTERVAL_SECONDS = 20;
    private static final int REPEAT_INSTRUCTION_THRESHOLD = 3;

    private static final int VIBRATE_INTERVAL_MINUTES = 2;
    private static final int VIBRATE_THRESHOLD = 5;

    public interface TaskInstructionView {

        void loadInstructionAudioFile(String audioFilePath);

        void showNextButton();

        void showRepeatButton();

        void updateProgress(int progress);

        void showEmptyView();

        void playInstruction();

        void cleanupPlaybackResources();

        void closeScreen();

        void vibrate();

        void showFinishButton();
    }

    private static final int UPDATE_RATIO_MILLISECONDS = 40;

    private final WearableManager wearableManager;
    private final TaskStatusEventBus taskStatusEventBus;

    private TaskInstructions taskInstructions;

    private int currentAudioDurationMilliseconds;
    private int currentPosition;

    private int repeatInstructionCounter;
    private Disposable repeatInstructionDisposable;

    private int vibrateCounter;
    private Disposable vibrateDisposable;

    private Disposable progressDisposable;

    @Inject
    public TaskInstructionPresenter(WearableManager wearableManager, TaskStatusEventBus taskStatusEventBus) {
        this.wearableManager = wearableManager;
        this.taskStatusEventBus = taskStatusEventBus;
    }

    public void start(TaskInstructions taskInstructions) {
        this.taskInstructions = taskInstructions;

        if (taskInstructions == null) {
            showEmptyView();
            return;
        }

        cleanupRepeatTimer();
        cleanupVibrateTimer();
        cleanupResources();
        currentPosition = 0;

        prepareInstructionPlayback();
        listenForTaskStatusEvents();
    }

    @Override
    public void stop() {
        super.stop();

        cleanupRepeatTimer();
        cleanupVibrateTimer();
        cleanupResources();
    }

    public void onScreenInvisible(){
        cancelTask();
    }

    public void onInstructionReady(int currentAudioDurationMilliseconds) {
        this.currentAudioDurationMilliseconds = currentAudioDurationMilliseconds;

        playInstruction();
    }

    private void playInstruction() {
        view.playInstruction();
        view.vibrate();
        showProgress();
    }

    public void onInstructionCompleted() {
        progressDisposable.dispose();

        if(currentPosition < taskInstructions.getInstructionFiles().length - 1){
            view.showNextButton();
        }else{
            view.showFinishButton();
        }

        startRepeatInstructionTimer();
    }

    public void onNextClicked() {
        cleanupRepeatTimer();

        if (currentPosition >= taskInstructions.getInstructionFiles().length - 1) {
            completeTask();
            return;
        }

        playNextInstruction();
    }

    public void onRepeatClicked() {
        cleanupRepeatTimer();
        cleanupVibrateTimer();

        playInstruction();
    }

    //region task status

    private void listenForTaskStatusEvents() {
        final Disposable disposable = taskStatusEventBus.toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskStatusReceived);

        addDisposable(disposable);
    }

    private void onTaskStatusReceived(TaskStatusEvent taskStatusEvent) {
        Log.d("STATUSEVENT", "taskStatusEvent event received");

        if (taskInstructions.getTaskId() == taskStatusEvent.getTaskId() && taskStatusEvent.isCanceled()) {
            wearableManager.cancelTask(taskInstructions, currentPosition)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            finishInstructions();
        }
    }

    //endregion

    //region empty view

    private void showEmptyView() {
        view.showEmptyView();
    }

    //endregion

    //region playback

    private void playNextInstruction() {
        currentPosition++;
        cleanupResources();
        prepareInstructionPlayback();
    }

    private void cleanupResources() {
        view.cleanupPlaybackResources();
    }

    private void prepareInstructionPlayback() {
        view.loadInstructionAudioFile(taskInstructions.getInstructionFiles()[currentPosition]);
    }

    private void showProgress() {
        progressDisposable = Observable
                .interval(UPDATE_RATIO_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateProgress);

        view.updateProgress(0);
    }

    private void updateProgress(long progress) {
        final long progressMilliseconds = progress * UPDATE_RATIO_MILLISECONDS;
        final long durationMilliseconds = currentAudioDurationMilliseconds;
        final double percentage = progressMilliseconds / (double) durationMilliseconds;

        view.updateProgress((int) (percentage * 100));
    }

    //endregion

    //region repeating

    private void startRepeatInstructionTimer() {
        repeatInstructionDisposable = Completable.timer(REPEAT_INSTRUCTION_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRepeatInstruction);
    }

    private void onRepeatInstruction() {
        repeatInstructionCounter++;
        if (repeatInstructionCounter < REPEAT_INSTRUCTION_THRESHOLD) {
            playInstruction();
            return;
        }

        showRepeatButton();
    }

    private void cleanupRepeatTimer() {
        if (repeatInstructionDisposable != null) {
            repeatInstructionDisposable.dispose();
        }
        repeatInstructionCounter = 0;
    }

    private void showRepeatButton() {
        view.showRepeatButton();

        vibrate();
    }

    private void vibrate() {
        vibrateCounter++;
        if (vibrateCounter >= VIBRATE_THRESHOLD) {
            cleanupVibrateTimer();

            cancelTask();

            return;
        }

        view.vibrate();

        vibrateDisposable = Completable.timer(VIBRATE_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::vibrate);
    }

    private void cleanupVibrateTimer() {
        if (vibrateDisposable != null) {
            vibrateDisposable.dispose();
        }

        vibrateCounter = 0;
    }

    //endregion

    //region task ending

    private void cancelTask() {
        if (taskInstructions == null) {
            return;
        }

        final Disposable disposable = wearableManager.cancelTask(taskInstructions, currentPosition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable()
                .subscribe(this::finishInstructions);

        addDisposable(disposable);
    }

    private void completeTask() {
        final Disposable disposable = wearableManager.completeTask(taskInstructions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable()
                .subscribe(this::finishInstructions);

        addDisposable(disposable);
    }

    private void finishInstructions() {
        cleanupRepeatTimer();
        cleanupVibrateTimer();

        view.cleanupPlaybackResources();
        view.closeScreen();
    }

    //endregion
}
