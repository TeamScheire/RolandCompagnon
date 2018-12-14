package be.imec.apt.bigfix.di;

import javax.inject.Singleton;

import be.imec.apt.bigfix.networking.CalendarApiService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

@Module
public class NetworkModule {
    private static final String GOOGLE_CALENDAR_BASE_URL = "https://calendar.google.com/calendar/ical/";

    private Retrofit.Builder createRetrofitBuilder() {
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .build();

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient);
    }

    @Singleton
    @Provides
    CalendarApiService provideCalendarService() {
        final Retrofit retrofit = createRetrofitBuilder()
                .baseUrl(GOOGLE_CALENDAR_BASE_URL)
                .build();
        return retrofit.create(CalendarApiService.class);
    }
}
