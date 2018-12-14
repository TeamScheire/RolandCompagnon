package be.imec.apt.bigfix.networking;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface CalendarApiService {

    @Streaming
    @GET("{calendarGroupId}/{calendarUserId}/basic.ics")
    Single<Response<ResponseBody>> downloadCalendarByUrl(@Path(value = "calendarGroupId", encoded = true) String calendarGroupId, @Path(value = "calendarUserId", encoded = true) String calendarUserId);
}
