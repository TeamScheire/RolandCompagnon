package be.imec.apt.bigfix.tasks;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import be.imec.apt.bigfix.database.entities.Event;

public class Task implements Parcelable{

    private final Event event;
    private final TaskInfo taskInfo;

    public Task(Event event, TaskInfo taskInfo) {
        this.event = event;
        this.taskInfo = taskInfo;
    }

    public Event getEvent() {
        return event;
    }

    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o instanceof Task) {
            Task task = (Task) o;
            return Objects.equals(event, task.event);
        } else if (o instanceof Event) {
            return Objects.equals(event, o);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    //region Parcelable

    protected Task(Parcel in) {
        event = in.readParcelable(Event.class.getClassLoader());
        taskInfo = in.readParcelable(TaskInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(event, flags);
        dest.writeParcelable(taskInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    //endregion
}
