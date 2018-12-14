package be.imec.apt.bigfix.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import be.imec.apt.bigfix.database.AppDatabase;
import dagger.Module;
import dagger.Provides;

@Module
public class PersistenceModule {

    @Provides
    @Singleton
    AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "calendar").build();
    }
}
