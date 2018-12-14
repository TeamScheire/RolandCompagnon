package be.imec.apt.bigfix.di;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import be.imec.apt.bigfix.general.BigFixApplication;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application application;

    public AppModule(BigFixApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
