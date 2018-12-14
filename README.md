# Roland Compagnon (aka "BigFix Dementia")
## Introduction
This repository contains the source code for an Android tablet app, and associated Wear OS smartwatch app, developed by [IMEC](https://imec-int.com) [APLL](https://maakdatmee.be) for the show [**Team Scheire**](https://www.canvas.be/team-scheire) (working title "Big [Life] Fix"). In this show, aired in Autumn 2018 on Flemish national television, a multidisciplinary team of makers/techies/scientists is invited to build solutions for people facing disabilities or other challenges. The software in this repository was created for the case of Roland & Christine.

Roland is in his fifties and has been diagnosed with young-age dementia. The tablet app helps him to remember where his wife Christine is, so that he doesn't panic when she goes out. It also helps him to complete tasks around the house. The tasks are triggered by a calendar, he can start them from a tablet. A smartwatch will then continue guidance through pre-recorded spoke instructions until the task is completed.

For more information, you can watch the first episode 1 of the first series of [Team Scheire](https://www.vrt.be/vrtnu/a-z/team-scheire/1/team-scheire).

## How to run
To run the project, import it in Android Studio.

Open the app's `build.gradle` and fill in the *Google Calendar ICAL* parameters in the staging / production flavor. 
You can find them in your Google Calendar settings: `Secret address in ical format`. The url is formatted as followed:

	https://calendar.google.com/calendar/ical/{groupId}/{userId}/basic.ics
	
You need two Google calendars, one for "*Chistine's*" calendar, shown on the top left in the tablet app, and one for "*Roland's*" task list shown on the bottom left. If you make your own version, please change the names of Christine and Roland to something else in `User.java`. If you want crash reporting to work you should also fill in an API key for [Fabric/Crashlytics](https://fabric.io/kits/android/crashlytics) in the `AndroidManifest.xml`.

### Calendar
For the calendar, the event's title, time & location is used in the app.

### Tasks
To define a task, they should be added to the task calendar. The type of tasks are defined in the app resources file `raw/tasks.json`. 
The Google Calendar event needs to have one of the defined tasks keywords in its description field (and nothing else). For example `honden_eten`. You can choose the event title freely. The event will trigger at the event's start time and will dismiss at the event's end time.

### Sync
The app syncs the calendars periodically, but you can also trigger a sync by pressing the bottom right **imec** logo

## Contact
For further questions, please contact [Lynn Coorevits](lynn.coorevits@imec.be) or [Matthias Stevens](matthias.stevens@imec.be).


## Legal
Copyright (2018) [IMEC](https://imec-int.com) vzw, Belgium.
Licensed under the terms of [Apache License v2.0](https://apache.org/licenses/LICENSE-2.0).
