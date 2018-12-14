package be.imec.apt.bigfix.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.WorkerThread;

import java.util.List;

import be.imec.apt.bigfix.database.entities.Event;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public abstract class CalendarDao {
    @Query("SELECT * FROM events WHERE uuid = :uuid AND day = :day AND user_id = :userId LIMIT 1")
    public abstract Single<Event> getEvent(String uuid, String userId, long day);

    @Query("SELECT * FROM events WHERE user_id = :userId AND day = :day")
    public abstract Single<List<Event>> getEventsForDay(String userId, Long day);

    @Query("SELECT * FROM events WHERE user_id = :userId AND end_time > :start AND start_time < :stop ORDER BY start_time")
    public abstract Flowable<List<Event>> getEvents(String userId, long start, long stop);

    @Query("SELECT * FROM events WHERE user_id = :userId AND day = :day ORDER BY start_time")
    public abstract Flowable<List<Event>> getEvents(String userId, long day);

    @Query("SELECT * FROM events WHERE user_id = :userId AND day = :day ORDER BY is_completed, completed_timestamp, start_time")
    public abstract Flowable<List<Event>> getTasks(String userId, long day);

    @Query("SELECT * FROM EVENTS where user_id = :userId AND :currentTime >= start_time AND :currentTime < end_time LIMIT 1")
    public abstract Single<Event> getEvent(String userId, long currentTime);

    @Query("SELECT * FROM EVENTS where user_id = :userId AND :currentTime >= start_time AND :currentTime < end_time AND is_started != 1 AND is_missed != 1 LIMIT 1")
    public abstract Single<Event> getEventWhichIsNotStarted(String userId, long currentTime);

    @WorkerThread
    @Query("SELECT * FROM events WHERE id = :eventId")
    public abstract Event getEvent(long eventId);

    @Query("SELECT * FROM events WHERE id = :eventId")
    public abstract Single<Event> getEventSingle(long eventId);

    @WorkerThread
    @Query("SELECT * FROM events WHERE user_id = :userId AND start_time >= :currentTime")
    public abstract List<Event> getFutureEvents(String userId, long currentTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertEvent(Event event);

    @Update
    public abstract void updateEvent(Event event);

    @Delete
    public abstract void removeEvents(List<Event> databaseEvents);
}
