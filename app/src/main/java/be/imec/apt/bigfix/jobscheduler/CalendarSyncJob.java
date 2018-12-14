package be.imec.apt.bigfix.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import javax.inject.Inject;

import be.imec.apt.bigfix.calendar.CalendarDataManager;
import be.imec.apt.bigfix.general.BigFixApplication;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CalendarSyncJob extends JobService {

    public static final int CALENDAR_SYNC_JOB_ID = 142150;

    @Inject
    CalendarDataManager calendarDataManager;

    private Disposable disposable;


    @Override
    public boolean onStartJob(JobParameters params) {
        BigFixApplication.appComponent.inject(this);

        Log.d(CalendarSyncJob.class.getSimpleName(), "Start calendar synchronization");
        this.disposable = calendarDataManager.synchronizeCalendar()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // Let the system reschedule
                .subscribe(result -> {
                    Log.d(CalendarSyncJob.class.getSimpleName(), "Finished calendar synchronization");
                    jobFinished(params, false);
                }, throwable -> {
                    Log.d(CalendarSyncJob.class.getSimpleName(), "Finished calendar synchronization with error: " + throwable.getMessage());
                    jobFinished(params, false);
                });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(CalendarSyncJob.class.getSimpleName(), "Unexpected stop in calendar synchronization");

        if (this.disposable != null) {
            disposable.dispose();
        }

        return true;
    }
}
