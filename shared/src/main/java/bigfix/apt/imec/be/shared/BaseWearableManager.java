package bigfix.apt.imec.be.shared;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;

public class BaseWearableManager {
    public static final String EXTRA_BYTES = "extraBytes";
    private static final String EXTRA_UNIQUE_BYTES = "extraUniqueBytes";

    private final Context context;

    public BaseWearableManager(Context context) {
        this.context = context;
    }

    public Single<Integer> sendMessage(final String path) {
        return sendMessage(path, null);
    }

    public Single<Integer> sendMessage(final String path, final byte[] bytes) {
        return Single.fromCallable(() -> {
            Task<List<Node>> wearableList = Wearable.getNodeClient(context).getConnectedNodes();

            try {
                List<Node> nodes = Tasks.await(wearableList);

                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(context).sendMessage(node.getId(), path, bytes);

                    //Block on a task and get the result synchronously, we assume there's only 1 device connected
                    return Tasks.await(sendMessageTask);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new WearableNotConnectedException();
            }

            throw new WearableNotConnectedException();
        });
    }

    public Single<DataItem> sendData(final String path, final byte[] bytes) {
        return Single.fromCallable(() -> {
            final PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
            putDataMapRequest.getDataMap().putByteArray(EXTRA_BYTES, bytes);
            putDataMapRequest.getDataMap().putLong(EXTRA_UNIQUE_BYTES, System.currentTimeMillis());
            putDataMapRequest.setUrgent();
            final Task<DataItem> sendDataTask = Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());

            //Block on a task and get the result synchronously, we assume there's only 1 device connected
            return Tasks.await(sendDataTask);
        });
    }
}
