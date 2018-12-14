package be.imec.apt.bigfix.jobscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

public class JobSchedulerUtils {

    private static final long CALENDAR_SYNC_INTERVAL = 10 * 60 * 1000; // 10 minutes

    public static void scheduleCalendarJob(final Context context) {
        final ComponentName jobComponentName = new ComponentName(context, CalendarSyncJob.class);
        final JobInfo jobInfo = new JobInfo.Builder(CalendarSyncJob.CALENDAR_SYNC_JOB_ID, jobComponentName)
                .setPersisted(true)
                .setPeriodic(CALENDAR_SYNC_INTERVAL)
                .build();

        final JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler == null) {
            throw new IllegalStateException("Jobscheduler not supported on this device");
        }

        scheduler.schedule(jobInfo);
    }
}
