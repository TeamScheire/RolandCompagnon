package bigfix.apt.imec.be.shared;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskInstructions implements Parcelable {
    private long taskId;
    private String[] instructionFiles;

    public TaskInstructions() {

    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String[] getInstructionFiles() {
        return instructionFiles;
    }

    public void setInstructionFiles(String[] instructionFiles) {
        this.instructionFiles = instructionFiles;
    }

    public TaskInstructions(Parcel in) {
        taskId = in.readLong();
        instructionFiles = in.createStringArray();
    }

    public static final Creator<TaskInstructions> CREATOR = new Creator<TaskInstructions>() {
        @Override
        public TaskInstructions createFromParcel(Parcel in) {
            return new TaskInstructions(in);
        }

        @Override
        public TaskInstructions[] newArray(int size) {
            return new TaskInstructions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(taskId);
        parcel.writeStringArray(instructionFiles);
    }
}
