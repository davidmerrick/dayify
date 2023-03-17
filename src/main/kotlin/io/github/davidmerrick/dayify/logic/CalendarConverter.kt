package io.github.davidmerrick.dayify.logic

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.DateEnd
import biweekly.property.DateStart
import biweekly.util.ICalDate
import java.time.temporal.ChronoUnit

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
}

fun ICalendar.copyWithEvents(events: List<VEvent>): ICalendar {
    val newCalendar = ICalendar(this)
    newCalendar.setComponent(VEvent::class.java, null)
    events.forEach { newCalendar.addEvent(it) }
    return newCalendar
}

fun convertStartDate(date: DateStart): DateStart {
    return DateStart(
        date.value.rawComponents.toDate(),
        false
    )
}

/**
 * Returns a Date that's 1 day after DateEnd.
 * This is a workaround for end dates being exclusive as per RFC 2445
 */
fun convertEndDate(dateEnd: DateEnd): DateEnd {
    val dateInstant = dateEnd.value.rawComponents
        .toDate()
        .toInstant()
        .plus(1L, ChronoUnit.DAYS)
    return DateEnd(
        ICalDate.from(dateInstant),
        false
    )
}

fun VEvent.stripTime(): VEvent {
    val newEvent = VEvent(this)

    // Strip out time components
    newEvent.dateStart = convertStartDate(this.dateStart)
    newEvent.dateEnd = convertEndDate(this.dateEnd)

    return newEvent
}
