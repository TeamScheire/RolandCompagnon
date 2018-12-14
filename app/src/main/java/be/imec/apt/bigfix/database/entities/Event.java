package be.imec.apt.bigfix.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.utils.DateUtils;
import be.imec.apt.bigfix.ical.CalendarEvent;

@Entity(tableName = "events", indices = {@Index(value = {"uuid", "day", "user_id"}, unique = true)})
public class Event implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "uuid")
    private String uuid;

    @ColumnInfo(name = "day")
    private long day;
    @ColumnInfo(name = "start_time")
    private long startTime;
    @ColumnInfo(name = "end_time")
    private long endTime;

    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "summary")
    private String summary;
    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "is_started")
    private boolean isStarted;
    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;
    @ColumnInfo(name = "completed_timestamp")
    private long completedTimestamp;
    @ColumnInfo(name = "is_missed")
    private boolean isMissed;
    @ColumnInfo(name = "is_stopped")
    private boolean isStopped;
    @ColumnInfo(name = "stop_position")
    private int stopPosition;


    public Event() {
    }

    public Event(CalendarEvent calendarEvent) {
        uuid = calendarEvent.getUuid();
        day = DateUtils.getStartOfDay(calendarEvent.getStartTime());
        startTime = calendarEvent.getStartTime();
        endTime = calendarEvent.getEndTime();
        description = calendarEvent.getDescription();
        summary = calendarEvent.getSummary();
        location = calendarEvent.getLocation();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(long completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }

    public boolean isMissed() {
        return isMissed;
    }

    public void setMissed(boolean missed) {
        isMissed = missed;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public int getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(int stopPosition) {
        this.stopPosition = stopPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            if (o != null && o.getClass() == Task.class) {
                return ((Task) o).getEvent().equals(this);
            } else {
                return false;
            }
        }

        Event event = (Event) o;
        return day == event.day &&
                Objects.equals(userId, event.userId) &&
                Objects.equals(uuid, event.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, userId, uuid);
    }

    //region Parcelable

    protected Event(Parcel in) {
        id = in.readLong();
        userId = in.readString();
        uuid = in.readString();
        day = in.readLong();
        startTime = in.readLong();
        endTime = in.readLong();
        description = in.readString();
        summary = in.readString();
        location = in.readString();
        isStarted = in.readInt() == 1;
        isCompleted = in.readInt() == 1;
        completedTimestamp = in.readLong();
        isMissed = in.readInt() == 1;
        isStopped = in.readInt() == 1;
        stopPosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(userId);
        dest.writeString(uuid);
        dest.writeLong(day);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeString(description);
        dest.writeString(summary);
        dest.writeString(location);
        dest.writeInt(isStarted ? 1 : 0);
        dest.writeInt(isCompleted ? 1 : 0);
        dest.writeLong(completedTimestamp);
        dest.writeInt(isMissed ? 1 : 0);
        dest.writeInt(isStopped ? 1 : 0);
        dest.writeInt(stopPosition);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    //endregion
}
