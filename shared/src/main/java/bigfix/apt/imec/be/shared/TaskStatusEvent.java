package bigfix.apt.imec.be.shared;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskStatusEvent implements Parcelable {
    private long taskId;
    private boolean isCompleted;
    private boolean isCanceled;
    private int taskPosition;

    public TaskStatusEvent() {
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getTaskPosition() {
        return taskPosition;
    }

    public void setTaskPosition(int taskPosition) {
        this.taskPosition = taskPosition;
    }

    //region Parcelable

    protected TaskStatusEvent(Parcel in) {
        taskId = in.readLong();
        isCanceled = in.readByte() != 0;
        isCompleted = in.readByte() != 0;
        taskPosition = in.readInt();
    }

    public static final Creator<TaskStatusEvent> CREATOR = new Creator<TaskStatusEvent>() {
        @Override
        public TaskStatusEvent createFromParcel(Parcel in) {
            return new TaskStatusEvent(in);
        }

        @Override
        public TaskStatusEvent[] newArray(int size) {
            return new TaskStatusEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(taskId);
        dest.writeByte((byte) (isCanceled ? 1 : 0));
        dest.writeByte((byte) (isCompleted ? 1 : 0));
        dest.writeInt(taskPosition);
    }

    //endregion
}
