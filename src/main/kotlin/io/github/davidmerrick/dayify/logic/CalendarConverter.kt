package io.github.davidmerrick.dayify.logic

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.DateEnd
import biweekly.property.DateStart
import java.time.temporal.ChronoUnit
import java.util.Date

/**
 * Note: By default, biweekly writes all date/time values in UTC time,
 * so no need to worry about conversions.
 */
object CalendarConverter {

    /**
     * Converts events to all-day events.
     * Returns a copy of the input calendar
     * containing the converted events.
     */
    fun convert(inCalendar: ICalendar): ICalendar {
        val convertedEvents = inCalendar.events
            .map { it.stripTime() }
            .toList()

        return inCalendar.copyWithEvents(convertedEvents)
    }

    /**
     * Returns a Date that's 1 day after DateEnd.
     * This is a workaround for end dates being exclusive as per RFC 2445
     */
    fun convertDateEnd(dateEnd: DateEnd): Date {
        val dateInstant = dateEnd.value.rawComponents
            .toDate()
            .toInstant()
            .plus(1L, ChronoUnit.DAYS)
        return Date.from(dateInstant)
    }
}

fun ICalendar.copyWithEvents(events: List<VEvent>): ICalendar {
    val newCalendar = ICalendar(this)
    newCalendar.setComponent(VEvent::class.java, null)
    events.forEach { newCalendar.addEvent(it) }
    return newCalendar
}

fun VEvent.stripTime(): VEvent {
    val newEvent = VEvent(this)

    // Strip out time components
    newEvent.dateStart = DateStart(
        this.dateStart.value.rawComponents.toDate(),
        false
    )

    newEvent.dateEnd = DateEnd(
        CalendarConverter.convertDateEnd(this.dateEnd),
        false
    )
    return newEvent
}
