package be.imec.apt.bigfix.ical;

import android.support.annotation.NonNull;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;

import org.apache.commons.collections4.Predicate;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import be.imec.apt.bigfix.utils.DateUtils;

/**
 * Parses an iCal file and creates TaskModels
 * Created by dvermeir on 07/12/2017.
 */

public class ICalParser {

    @Inject
    public ICalParser() {
    }

    /**
     * Parse an iCal file
     *
     * @param icalFile The file containing the calendar info
     * @param startDate Start date to parse events for
     * @param days number of days to parse
     * @return A list of calendar events parsed from the file
     */
    public List<CalendarEvent> parse(File icalFile, @NonNull Date startDate, int days) {
        List<CalendarEvent> tasks = new ArrayList<>();
        if (icalFile == null) {
            Log.d(getClass().getName(), "No iCal file available");
            return tasks;
        }
        try {
            FileInputStream fin = new FileInputStream(icalFile);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fin);
            List<VEvent> list = filterDays(calendar, startDate);
            tasks = serializeEvents(list, startDate, days);
        } catch (FileNotFoundException ex) {
            Log.e(getClass().getName(), "iCal not found", ex);
        } catch (ParserException ex) {
            Log.e(getClass().getName(), "Error parsing iCal", ex);
        } catch (IOException ex) {
            Log.e(getClass().getName(), "Error reading iCal", ex);
        }

        return tasks;
    }

    private Period getCalendarDaysPeriod(Date startDate, int durationInDays) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(startDate.getTime());
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        return new Period(new DateTime(cal.getTime()), new Dur(durationInDays, 0, 0, 0));
    }

    /**
     * Gets the events happening today
     *
     * @param cal The parsed iCal to filter
     * @return A list of events
     */
    private List<VEvent> filterDays(Calendar cal, Date startDate) {
        Predicate[] rules = new Predicate[]{new PeriodRule(getCalendarDaysPeriod(startDate, 2)), new PrivateEventRule()};
        @SuppressWarnings("unchecked")
        Filter filter = new Filter(rules, Filter.MATCH_ALL);

        //noinspection unchecked
        return (List) filter.filter(cal.getComponents(Component.VEVENT));
    }

    /**
     * Serializes calendar events to TaskTaskModel
     *
     * @param events List of parsed events
     * @param startDate Start date of the events to serizalize
     * @param days amount of days to serialize starting on startDate
     * @return Returns a List of TaskModels
     */
    private List<CalendarEvent> serializeEvents(List<VEvent> events, Date startDate, int days) {
        List<CalendarEvent> calendarEvents = new ArrayList<>();
        Map<String, CalendarEvent> uniqueCalendarEvents = new HashMap<>();
        int i = 0;
        for (VEvent event : events) {
            //Get recurring events
            PeriodList eventPeriodList = event.calculateRecurrenceSet(getCalendarDaysPeriod(startDate, days));
            for (Period eventPeriod : eventPeriodList) {
                String uuid = event.getUid().toString();

                Summary summary = event.getSummary();
                Location location = event.getLocation();

                // Strip html
                Description desc = event.getDescription();
                final String description = Jsoup.parse(desc.getValue()).text();

                CalendarEvent calendarEvent = new CalendarEvent();
                calendarEvent.setStartTime(eventPeriod.getStart().getTime());
                calendarEvent.setEndTime(eventPeriod.getEnd().getTime());
                calendarEvent.setUuid(uuid);
                calendarEvent.setSummary(summary.getValue());
                calendarEvent.setLocation(location.getValue());
                calendarEvent.setDescription(description);
                calendarEvent.setSequence(event.getSequence().getValue());

                // Moved recurring events still appear duplicate in the list, make sure we only keep the one with the latest sequence number.
                // Unfortunately this won't work for recurrent events on the same day, or daily recurrent events which are moved afterwards to
                // another day of the same recurrent event
                final String uniqueCalendarEventKey = uuid + DateUtils.getStartOfDay(eventPeriod.getStart().getTime());
                final CalendarEvent duplicateCalendarEvent = uniqueCalendarEvents.get(uniqueCalendarEventKey);
                if (duplicateCalendarEvent != null) {
                    if (calendarEvent.getSequence().compareTo(duplicateCalendarEvent.getSequence()) > 0) {
                        int oldIndex = calendarEvents.indexOf(duplicateCalendarEvent);
                        calendarEvents.set(oldIndex, calendarEvent);
                        uniqueCalendarEvents.put(uniqueCalendarEventKey, calendarEvent);
                    } else {
                        continue;
                    }
                } else {
                    calendarEvents.add(calendarEvent);
                    uniqueCalendarEvents.put(uniqueCalendarEventKey, calendarEvent);
                }

                Log.d("CalendarEvent", summary.getValue() + ": " + DateUtils.formatDateTime(eventPeriod.getStart().getTime()) + " - " + DateUtils.formatDateTime(eventPeriod.getEnd().getTime()) + "; sequence: " + event.getSequence());

                i++;
            }
        }
        return calendarEvents;
    }
}
