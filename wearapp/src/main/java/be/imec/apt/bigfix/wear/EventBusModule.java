package be.imec.apt.bigfix.wear;

import javax.inject.Singleton;

import bigfix.apt.imec.be.shared.TaskStatusEventBus;
import dagger.Module;
import dagger.Provides;

@Module
public class EventBusModule {
    @Provides
    @Singleton
    TaskStatusEventBus provideTaskEventStatusBus() {
        return new TaskStatusEventBus();
    }
}
