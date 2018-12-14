package be.imec.apt.bigfix.tasks;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TaskInfo implements Parcelable {
    @SerializedName("icon")
    private final
    String iconPath;

    @SerializedName("animation")
    final
    String animationPath;

    @SerializedName("instructions")
    final
    Instruction[] instructions;

    public String getIconPath() {
        return iconPath;
    }

    public String getAnimationPath() {
        return animationPath;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    //region Parcelable

    private TaskInfo(Parcel in) {
        iconPath = in.readString();
        animationPath = in.readString();
        instructions = in.createTypedArray(Instruction.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iconPath);
        dest.writeString(animationPath);
        dest.writeTypedArray(instructions, 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {
        @Override
        public TaskInfo createFromParcel(Parcel in) {
            return new TaskInfo(in);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }
    };

    //endregion

    public static class Instruction implements Parcelable {
        @SerializedName("image")
        final
        String image;

        @SerializedName("description")
        final
        String description;

        @SerializedName("audio")
        final
        String audio;

        public String getAudio() {
            return audio;
        }

        //region Parcelable

        Instruction(Parcel in) {
            image = in.readString();
            description = in.readString();
            audio = in.readString();
        }

        public static final Creator<Instruction> CREATOR = new Creator<Instruction>() {
            @Override
            public Instruction createFromParcel(Parcel in) {
                return new Instruction(in);
            }

            @Override
            public Instruction[] newArray(int size) {
                return new Instruction[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(image);
            dest.writeString(description);
            dest.writeString(audio);
        }

        //endregion
    }
}
