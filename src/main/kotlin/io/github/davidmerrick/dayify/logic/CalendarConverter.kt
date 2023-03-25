package io.github.davidmerrick.dayify.logic

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.DateEnd
import biweekly.property.DateStart
import biweekly.util.ICalDate
import java.time.temporal.ChronoUnit

/**
 * Note: By default, biweekly writes all date/time values in UTC time
 */
object CalendarConverter {

    /**
     * Converts events to all-day events.
     * Returns a copy of the input calendar
     * containing the converted events.
     */
    fun convert(inCalendar: ICalendar): ICalendar {
        return inCalendar.events
            .map { stripTime(it) }
            .toList()
            .let {
                inCalendar.copyWithEvents(it)
            }

    }

    private fun convertStartDate(date: DateStart) = DateStart(date.value.rawComponents.toDate(), false)

    /**
     * Returns a Date that's 1 day after DateEnd.
     * Note that end dates are exclusive as per RFC 2445
     */
    private fun convertEndDate(dateEnd: DateEnd): DateEnd {
        // Truncate the end date to days, then add 1 day to it
        return dateEnd.value
            .toInstant()
            .truncatedTo(ChronoUnit.DAYS)
            .plus(1, ChronoUnit.DAYS)
            .let { DateEnd(ICalDate.from(it), false) }
    }

    private fun stripTime(event: VEvent): VEvent {
        val newEvent = VEvent(event)

        // Strip out time components
        newEvent.dateStart = convertStartDate(event.dateStart)
        newEvent.dateEnd = convertEndDate(event.dateEnd)

        return newEvent
    }
}

fun ICalendar.copyWithEvents(events: List<VEvent>): ICalendar {
    val newCalendar = ICalendar(this)
    newCalendar.timezoneInfo.defaultTimezone = null
    newCalendar.setComponent(VEvent::class.java, null)
    events.forEach { newCalendar.addEvent(it) }
    return newCalendar
}
