package be.imec.apt.bigfix.wear;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import javax.inject.Inject;

import bigfix.apt.imec.be.shared.BaseWearableManager;
import bigfix.apt.imec.be.shared.ParcelableUtils;
import bigfix.apt.imec.be.shared.TaskInstructions;
import bigfix.apt.imec.be.shared.TaskStatusEvent;
import bigfix.apt.imec.be.shared.TaskStatusEventBus;

public class MessageReceiverService extends WearableListenerService {

    @Inject
    TaskStatusEventBus taskStatusEventBus;

    @Override
    public void onCreate() {
        super.onCreate();

        WearableApplication.appComponent.inject(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/task/started")) {
            final Parcel parcel = ParcelableUtils.unmarshall(messageEvent.getData());
            final TaskInstructions taskInstructions = TaskInstructions.CREATOR.createFromParcel(parcel);

            final Intent intent = TaskInstructionActivity.getIntent(this, taskInstructions);
            startActivity(intent);
            return;
        }

        super.onMessageReceived(messageEvent);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getDataItem().getUri().getPath().equals("/task/cancel")) {
                final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final DataMap dataMap = dataMapItem.getDataMap();
                final Parcel parcel = ParcelableUtils.unmarshall(dataMap.getByteArray(BaseWearableManager.EXTRA_BYTES));
                final TaskStatusEvent taskStatusEvent = TaskStatusEvent.CREATOR.createFromParcel(parcel);

                taskStatusEventBus.send(taskStatusEvent);
            }
        }
    }
}
