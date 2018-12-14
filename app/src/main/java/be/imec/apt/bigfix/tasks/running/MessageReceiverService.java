package be.imec.apt.bigfix.tasks.running;

import android.os.Parcel;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.general.BigFixApplication;
import bigfix.apt.imec.be.shared.BaseWearableManager;
import bigfix.apt.imec.be.shared.ParcelableUtils;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import bigfix.apt.imec.be.shared.TaskStatusEventBus;

public class MessageReceiverService extends WearableListenerService {

    @Inject
    TaskStatusEventBus taskStatusEventBus;

    @Inject
    CalendarDataManager calendarDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        BigFixApplication.appComponent.inject(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getDataItem().getUri().getPath().equals("/task/completed")) {

                final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final DataMap dataMap = dataMapItem.getDataMap();

                final Parcel parcel = ParcelableUtils.unmarshall(dataMap.getByteArray(BaseWearableManager.EXTRA_BYTES));
                final TaskStatusEvent taskStatusEvent = TaskStatusEvent.CREATOR.createFromParcel(parcel);

                taskStatusEventBus.send(taskStatusEvent);
            } else if (event.getDataItem().getUri().getPath().equals("/task/canceled")) {
                final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final DataMap dataMap = dataMapItem.getDataMap();

                final Parcel parcel = ParcelableUtils.unmarshall(dataMap.getByteArray(BaseWearableManager.EXTRA_BYTES));
                final TaskStatusEvent taskStatusEvent = TaskStatusEvent.CREATOR.createFromParcel(parcel);

                calendarDataManager.saveStopPosition(taskStatusEvent.getTaskId(), taskStatusEvent.getTaskPosition());

                taskStatusEventBus.send(taskStatusEvent);
            }
        }
    }
}
