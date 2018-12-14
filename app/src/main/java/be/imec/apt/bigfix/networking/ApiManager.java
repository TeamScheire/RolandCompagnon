package be.imec.apt.bigfix.networking;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.User;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ApiManager {

    private final CalendarApiService calendarApiService;

    @Inject
    public ApiManager(CalendarApiService calendarApiService) {
        this.calendarApiService = calendarApiService;
    }

    public Single<ResponseBody> downloadCalendar(User user) {
        return calendarApiService.downloadCalendarByUrl(user.getCalendarGroupId(), user.getCalendarUserId())
                .map(response -> {
                    if (response.errorBody() != null) {
                        throw new HttpException(response);
                    }

                    final ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        throw new EmptyBodyException();
                    }

                    return responseBody;
                });
    }
}
