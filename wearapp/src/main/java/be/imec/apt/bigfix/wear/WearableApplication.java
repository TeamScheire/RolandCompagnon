package be.imec.apt.bigfix.wear;

import android.app.Application;

public class WearableApplication extends Application{

    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        initDagger();
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
