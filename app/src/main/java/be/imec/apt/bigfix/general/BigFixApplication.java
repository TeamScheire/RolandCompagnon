package be.imec.apt.bigfix.general;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import com.facebook.stetho.Stetho;

import be.imec.apt.bigfix.di.AppComponent;
import be.imec.apt.bigfix.di.AppModule;
import be.imec.apt.bigfix.di.DaggerAppComponent;
import be.imec.apt.bigfix.jobscheduler.JobSchedulerUtils;

import io.fabric.sdk.android.Fabric;

public class BigFixApplication extends Application {

    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Fabric:
        Fabric.with(this, new Crashlytics());

        initDagger();
        initStetho();
        initJobSchedulers();
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void initJobSchedulers() {
        JobSchedulerUtils.scheduleCalendarJob(this);
    }
}
