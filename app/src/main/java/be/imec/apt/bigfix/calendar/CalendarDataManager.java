package be.imec.apt.bigfix.calendar;

import android.arch.persistence.room.EmptyResultSetException;
import android.content.Context;
import android.support.annotation.WorkerThread;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import be.imec.apt.bigfix.notifications.NotificationManager;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.ical.CalendarEvent;
import be.imec.apt.bigfix.ical.ICalParser;
import be.imec.apt.bigfix.database.AppDatabase;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.networking.ApiManager;
import be.imec.apt.bigfix.storage.FileManager;
import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.tasks.TaskInfo;
import be.imec.apt.bigfix.utils.DateUtils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class CalendarDataManager {
    private final static String FILENAME = "calendar.ics";

    private final Context context;
    private final ApiManager apiManager;
    private final FileManager fileManager;
    private final ICalParser icalParser;
    private final AppDatabase database;
    private final NotificationManager notificationManager;

    @Inject
    public CalendarDataManager(Context context, ApiManager apiManager, FileManager fileManager, ICalParser iCalParser, AppDatabase database, NotificationManager notificationManager) {
        this.context = context;
        this.apiManager = apiManager;
        this.fileManager = fileManager;
        this.icalParser = iCalParser;
        this.database = database;
        this.notificationManager = notificationManager;
    }

    public Single<HashMap<User, List<Event>>> synchronizeCalendar() {
        final User[] users = User.values();

        return Observable
                .fromArray(users)
                // Handle 1 user
                .flatMap((Function<User, ObservableSource<UserCalendar>>) user ->
                        // Synchronize calendar for user
                        apiManager.downloadCalendar(user)
                                .flatMap(this::saveToFile)
                                .flatMap(this::parseCalendar)
                                .flatMap((Function<List<CalendarEvent>, SingleSource<List<Event>>>) calendarEvents -> mergeEventsInDatabase(calendarEvents, user))
                                .map(events -> new UserCalendar(user, events))
                                .toObservable()
                )
                // Join users & calendars in map
                .collect(HashMap<User, List<Event>>::new, (userListHashMap, userCalendar) -> userListHashMap.put(userCalendar.user, userCalendar.events));

    }

    private Single<File> saveToFile(ResponseBody responseBody) throws IOException {
        File file = new File(context.getCacheDir(), System.currentTimeMillis() + "_" + FILENAME);

        return fileManager.saveToDisk(file, responseBody.source());
    }

    private Single<List<CalendarEvent>> parseCalendar(File calendarFile) {
        if (!calendarFile.exists()) {
            return Single.error(new FileNotFoundException());
        }

        final List<CalendarEvent> tasks = icalParser.parse(calendarFile, new Date(), 2);

        //noinspection ResultOfMethodCallIgnored files in cache folder will be cleaned up by the system
        calendarFile.delete();

        return Single.just(tasks);
    }

    private Single<List<Event>> mergeEventsInDatabase(List<CalendarEvent> calendarEvents, User user) {
        return fileManager.getTasks().flatMap((Function<Map<String, TaskInfo>, SingleSource<List<Event>>>) taskInfoMap -> Observable
                // Handle event one by one
                .fromIterable(calendarEvents)
                .flatMap((Function<CalendarEvent, ObservableSource<Event>>) calendarEvent -> {
                    final Event event = new Event(calendarEvent);
                    event.setUserId(user.getId());

                    // Find the same event in database
                    return database.calendarDao().getEvent(event.getUuid(), user.getId(), event.getDay())
                            .flatMap((Function<Event, SingleSource<Event>>) databaseEvent -> {
                                // Event is already in database, copy user saved data in new event & replace it
                                event.setId(databaseEvent.getId());
                                event.setStarted(databaseEvent.isStarted());
                                event.setCompleted(databaseEvent.isCompleted());
                                event.setCompletedTimestamp(databaseEvent.getCompletedTimestamp());
                                event.setMissed(databaseEvent.isMissed());
                                event.setStopped(databaseEvent.isStopped());
                                event.setStopPosition(databaseEvent.getStopPosition());

                                database.calendarDao().updateEvent(event);

                                if (user == User.ROLAND &&
                                        (event.getStartTime() != databaseEvent.getStartTime() || event.getEndTime() != databaseEvent.getEndTime())
                                        && !event.isStarted() && !event.isMissed()) {
                                    //timestamps changed, remove notifications from old event and add the notifications for updated event
                                    notificationManager.unscheduleNotifications(databaseEvent);
                                    if (taskInfoMap.containsKey(event.getDescription())) {
                                        notificationManager.scheduleNotifications(event);
                                    }
                                }

                                return Single.just(event);
                            })
                            .onErrorResumeNext(throwable -> {
                                if (throwable instanceof EmptyResultSetException) {
                                    // Event is not yet in database, so add it
                                    final long id = database.calendarDao().insertEvent(event);
                                    event.setId(id);

                                    if (user == User.ROLAND && taskInfoMap.containsKey(event.getDescription())) {
                                        //schedule notifications for this event
                                        notificationManager.scheduleNotifications(event);
                                    }

                                    return Single.just(event);
                                }

                                // Something else went wrong, this should not happen... abort!
                                return Single.error(throwable);
                            })
                            .toObservable();
                })
                // Group events again by day
                .collect((Callable<Map<Long, List<Event>>>) HashMap::new, (map, event) -> {
                    if (map.containsKey(event.getDay())) {
                        map.get(event.getDay()).add(event);
                    } else {
                        final List<Event> eventList = new ArrayList<>();
                        eventList.add(event);
                        map.put(event.getDay(), eventList);
                    }
                })
                .map(map -> {
                    final long startOfToday = DateUtils.getStartOfDay(System.currentTimeMillis());
                    final long nextDay = DateUtils.getStartOfDayWithOffset(System.currentTimeMillis(), 1);

                    if (!map.containsKey(startOfToday)) {
                        map.put(startOfToday, new ArrayList<>());
                    }
                    if (!map.containsKey(nextDay)) {
                        map.put(nextDay, new ArrayList<>());
                    }

                    return map;
                })
                // Remove the remote deleted events from database
                .flatMap((Function<Map<Long, List<Event>>, SingleSource<List<Event>>>) eventsPerDayMap -> removeDeletedEvents(user, eventsPerDayMap)));
    }

    private Single<List<Event>> removeDeletedEvents(User user, Map<Long, List<Event>> eventsPerDayMap) {
        return Observable
                // Remove events per day
                .fromArray(eventsPerDayMap.keySet().toArray(new Long[eventsPerDayMap.size()]))
                .flatMap((Function<Long, ObservableSource<List<Event>>>) day ->
                        // Get events of day in database
                        database.calendarDao().getEventsForDay(user.getId(), day)
                                .map(databaseEvents -> {
                                    // Create a list of items which should be removed from database
                                    final List<Event> itemsToRemove = new ArrayList<>(databaseEvents);
                                    final List<Event> itemsForDay = eventsPerDayMap.get(day);
                                    itemsToRemove.removeAll(itemsForDay);

                                    // Remove the items
                                    database.calendarDao().removeEvents(itemsToRemove);

                                    if (user == User.ROLAND) {
                                        //remove the notifications for the deleted events
                                        notificationManager.unscheduleNotifications(itemsToRemove);
                                    }

                                    return itemsForDay;
                                })
                                .toObservable()
                )
                // Collect all events currently in database, for multiple days
                .collect(ArrayList::new, List::addAll);
    }

    public Flowable<List<Event>> getCalendar(long startTime, long endTime) {
        return database.calendarDao().getEvents(User.CHRISTINE.getId(), startTime, endTime);
    }

    public Single<Event> getCurrentEvent() {
        return database.calendarDao().getEvent(User.CHRISTINE.getId(), System.currentTimeMillis());
    }

    public Single<Event> getCurrentTask() {
        return database.calendarDao().getEventWhichIsNotStarted(User.ROLAND.getId(), System.currentTimeMillis());
    }

    public Flowable<List<Task>> getTasks(long day) {
        final Flowable<List<Event>> eventsFlowable = database.calendarDao().getTasks(User.ROLAND.getId(), day);
        return eventsFlowable.flatMap((Function<List<Event>, Publisher<List<Task>>>) events -> fileManager.getTasks()
                .flatMapPublisher((Function<Map<String, TaskInfo>, Publisher<List<Task>>>) taskInfoMap -> {
                    final List<Task> taskList = new ArrayList<>();
                    for (Event event : events) {
                        try {
                            final TaskInfo taskInfo = taskInfoMap.get(event.getDescription());
                            if (taskInfo != null) {
                                taskList.add(new Task(event, taskInfo));
                            }
                        } catch (IllegalArgumentException ignored) {

                        }
                    }
                    return Flowable.just(taskList);
                })
        );
    }

    @WorkerThread
    public List<Event> getFutureEvents(User user, long currentTime) {
        return database.calendarDao().getFutureEvents(user.getId(), currentTime);
    }

    public Single<Task> getEvent(long eventId) {
        final Single<Event> eventSingle = database.calendarDao().getEventSingle(eventId);
        final Single<Map<String, TaskInfo>> tasksSingle = fileManager.getTasks();

        return Single.zip(eventSingle, tasksSingle, (event, taskInfoMap) -> {
            final TaskInfo taskInfo = taskInfoMap.get(event.getDescription());
            if (taskInfo != null) {
                return new Task(event, taskInfo);
            }

            throw new EmptyResultSetException("Task info not found");
        });
    }

    public Completable startTask(Task currentTask) {
        return Completable.fromAction(() -> {
            final Event event = currentTask.getEvent();
            event.setStarted(true);

            database.calendarDao().updateEvent(event);

            notificationManager.unscheduleNotifications(currentTask.getEvent());
        });
    }

    public Completable completeTask(Task task) {
        return Completable.fromAction(() -> {
            final Event event = task.getEvent();
            event.setCompleted(true);
            event.setCompletedTimestamp(System.currentTimeMillis());

            database.calendarDao().updateEvent(event);
        });
    }

    public Completable ignoreTask(final Event event) {
        return Completable.fromAction(() -> {
            event.setMissed(true);

            database.calendarDao().updateEvent(event);

            notificationManager.unscheduleNotifications(event);
        });
    }

    public Completable stopTask(final Task task) {
        return Completable.fromAction(() -> {
            final Event event = task.getEvent();
            event.setStopped(true);

            database.calendarDao().updateEvent(event);
        });
    }

    public void saveStopPosition(long taskId, int taskPosition) {
        final Event event = database.calendarDao().getEvent(taskId);
        event.setStopPosition(taskPosition);

        database.calendarDao().updateEvent(event);
    }

    private static final class UserCalendar {
        final User user;
        final List<Event> events;

        UserCalendar(User user, List<Event> events) {
            this.user = user;
            this.events = events;
        }
    }
}
