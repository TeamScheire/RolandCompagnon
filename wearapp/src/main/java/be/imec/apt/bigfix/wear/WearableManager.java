package be.imec.apt.bigfix.wear;

import android.content.Context;

import com.google.android.gms.wearable.DataItem;

import javax.inject.Inject;

import bigfix.apt.imec.be.shared.BaseWearableManager;
import bigfix.apt.imec.be.shared.ParcelableUtils;
import bigfix.apt.imec.be.shared.TaskInstructions;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import io.reactivex.Single;

public class WearableManager extends BaseWearableManager {

    @Inject
    public WearableManager(Context context) {
        super(context);
    }

    public Single<DataItem> completeTask(TaskInstructions taskInstructions) {
        TaskStatusEvent taskStatusEvent = new TaskStatusEvent();
        taskStatusEvent.setTaskId(taskInstructions.getTaskId());
        taskStatusEvent.setCompleted(true);

        return sendData("/task/completed", ParcelableUtils.marshall(taskStatusEvent));
    }

    public Single<DataItem> cancelTask(TaskInstructions taskInstructions, int currentPosition) {
        TaskStatusEvent taskStatusEvent = new TaskStatusEvent();
        taskStatusEvent.setTaskId(taskInstructions.getTaskId());
        taskStatusEvent.setTaskPosition(currentPosition);
        taskStatusEvent.setCanceled(true);

        return sendData("/task/canceled", ParcelableUtils.marshall(taskStatusEvent));
    }
}
