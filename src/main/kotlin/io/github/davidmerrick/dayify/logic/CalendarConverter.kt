package io.github.davidmerrick.dayify.logic

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.property.DateEnd
import biweekly.property.DateStart
import biweekly.util.ICalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Note: By default, biweekly writes all date/time values in UTC time
 */
object CalendarConverter {

    private val defaultZone = ZoneId.of("UTC")

    /**
     * Converts events to all-day events.
     * Returns a copy of the input calendar
     * containing the converted events.
     */
    fun convert(inCalendar: ICalendar, zoneId: ZoneId? = null): ICalendar {
        return inCalendar.events
            .map { stripTime(it, zoneId ?: defaultZone) }
            .toList()
            .let {
                inCalendar.copyWithEvents(it)
            }

    }

    private fun convertStartDate(date: DateStart) = DateStart(date.value, false)

    /**
     * Returns a Date that's 1 day after DateEnd.
     * Note that end dates are exclusive as per RFC 2445
     */
    private fun convertEndDate(dateEnd: DateEnd, zoneId: ZoneId): DateEnd {
        // Create a UTC date at the given zone before adding a day to it
        val normalized = dateEnd.value
            .toInstant()
            .atZone(zoneId)
            .truncatedTo(ChronoUnit.DAYS)

        return normalized.plus(1, ChronoUnit.DAYS)
            .toInstant()
            .let { DateEnd(ICalDate.from(it), false) }
    }

    private fun stripTime(event: VEvent, zoneId: ZoneId): VEvent {
        val newEvent = VEvent(event)

        // Strip out time components
        newEvent.dateStart = convertStartDate(event.dateStart)
        newEvent.dateEnd = convertEndDate(event.dateEnd, zoneId)

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
