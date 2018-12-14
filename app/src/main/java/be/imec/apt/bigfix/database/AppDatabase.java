package be.imec.apt.bigfix.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import be.imec.apt.bigfix.database.entities.Event;

@Database(entities = {Event.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalendarDao calendarDao();
}
