Ever subscribe to a calendar with multi-day events, and they're not set to be all-day events
so they block your entire goddamn calendar? Does that annoy you?

![](/docs/img/screenshot.png)

Well it grinds my gears. That's why I created Dayify.

It's a web service that takes a calendar url and 
gives you back the same calendar with the events
converted to all-day events.

![](/docs/img/screenshot-after.png)

Ah, that's better. 

Better living, with Dayify™.

Try it out: 
https://dayify-gy2rtbjq4q-uw.a.run.app/dayify?url=yourCalendarUrl

Pro tip: Pass Dayify a calendar you want to subscribe to, have Dayify proxy it, and subscribe to the Dayify URL to give all future events that all-day treatment.

# Notes

According to RFC 2445, the behavior around end dates is exclusive:

> The "DTEND" property for a "VEVENT" calendar component specifies the non-inclusive end of the event.

Meaning if you had an event that ended on Monday the 21st at noon, but converted that to an all-day event,
the behavior would be to show the event as going through Sunday. This is clearly undesired behavior,
so Dayify rounds end dates to the next day.

# Todo: 
* Have the client send a user agent
* Have the client follow 302 redirects
