package be.imec.apt.bigfix.di;

import javax.inject.Singleton;

import be.imec.apt.bigfix.database.AppDatabase;
import be.imec.apt.bigfix.notifications.TaskEventBus;
import bigfix.apt.imec.be.shared.TaskStatusEventBus;
import dagger.Module;
import dagger.Provides;

@Module
public class EventBusModule {
    @Provides
    @Singleton
    TimeTickBus provideTimeTickBus() {
        return new TimeTickBus();
    }

    @Provides
    @Singleton
    DateChangeBus provideDateChangeBus() {
        return new DateChangeBus();
    }

    @Provides
    @Singleton
    TaskEventBus provideTaskEventBus(AppDatabase appDatabase) {
        return new TaskEventBus(appDatabase);
    }

    @Provides
    @Singleton
    TaskStatusEventBus provideTaskEventStatusBus() {
        return new TaskStatusEventBus();
    }
}
