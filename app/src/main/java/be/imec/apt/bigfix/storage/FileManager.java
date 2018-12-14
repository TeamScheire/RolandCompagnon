package be.imec.apt.bigfix.storage;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.tasks.TaskInfo;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileManager {

    private final Context context;

    private final Gson gson;

    @Inject
    public FileManager(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public Single<File> saveToDisk(File file, BufferedSource source) throws IOException {
        final BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeAll(source);
        sink.close();

        return Single.just(file);
    }

    public Single<Map<String, TaskInfo>> getTasks() {
        return Single.defer((Callable<SingleSource<Map<String, TaskInfo>>>) () -> {
            final Type type = new TypeToken<Map<String, TaskInfo>>() {
            }.getType();

            final InputStream inputStream = context.getResources().openRawResource(R.raw.tasks);
            final Map<String, TaskInfo> tasks = gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), type);
            return Single.just(tasks);
        });
    }
}
