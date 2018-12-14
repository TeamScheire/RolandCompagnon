package be.imec.apt.bigfix.tasks.running;

import android.content.Context;

import javax.inject.Inject;

import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.tasks.TaskInfo;
import bigfix.apt.imec.be.shared.BaseWearableManager;
import bigfix.apt.imec.be.shared.ParcelableUtils;
import bigfix.apt.imec.be.shared.TaskInstructions;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import io.reactivex.Completable;
import io.reactivex.Single;

public class WearableManager extends BaseWearableManager {

    @Inject
    public WearableManager(Context context) {
        super(context);
    }

    public Single<Integer> startTask(Task task) {
        final TaskInstructions taskInstructions = convertTaskToTaskInstructions(task);
        final byte[] bytes = ParcelableUtils.marshall(taskInstructions);

        return sendMessage("/task/started", bytes);
    }

    public Completable cancelTask(Task task) {
        final TaskStatusEvent taskStatusEvent = new TaskStatusEvent();
        taskStatusEvent.setTaskId(task.getEvent().getId());
        taskStatusEvent.setCanceled(true);

        return sendData("/task/cancel", ParcelableUtils.marshall(taskStatusEvent))
                .toCompletable();
    }

    private TaskInstructions convertTaskToTaskInstructions(final Task task) {
        final TaskInfo.Instruction[] instructions = task.getTaskInfo().getInstructions();
        final String[] instructionFiles = new String[instructions.length];
        for (int i = 0; i < instructions.length; i++) {
            instructionFiles[i] = instructions[i].getAudio();
        }

        final TaskInstructions taskInstructions = new TaskInstructions();
        taskInstructions.setTaskId(task.getEvent().getId());
        taskInstructions.setInstructionFiles(instructionFiles);

        return taskInstructions;
    }
}
