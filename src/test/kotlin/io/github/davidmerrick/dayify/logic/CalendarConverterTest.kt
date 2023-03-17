package io.github.davidmerrick.dayify.logic

import biweekly.Biweekly
import biweekly.util.ICalDate
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

private const val ON_CALL_CALENDAR_FILENAME = "/on_call_schedule.ics"
private const val SINGLE_DAY_EVENTS_CALENDAR_FILENAME = "/single_day_events.ics"

class CalendarConverterTest {
    @Test
    fun `Parse calendar and convert to all-day events`() {
        val calendarString = this::class.java.getResource(ON_CALL_CALENDAR_FILENAME)
            .readText(Charsets.UTF_8)
        val inCalendar = Biweekly.parse(calendarString).first()
        val outCalendar = CalendarConverter.convert(inCalendar)
        outCalendar.events.forEach {
            it.dateStart.value.hasTime() shouldBe false
            it.dateEnd.value.hasTime() shouldBe false
        }

        outCalendar.events.size shouldBe inCalendar.events.size
        val fooEvent = outCalendar.events.first { it.summary.value == "Foo event" }
        assertDatesMatch(2020, 11, 4, toZonedDate(fooEvent.dateStart.value))
        assertDatesMatch(2020, 11, 12, toZonedDate(fooEvent.dateEnd.value))
    }

    @Test
    fun `Parse Nursegrid webcal calendar and convert to all-day events`() {
        val calendarString = this::class.java.getResource(SINGLE_DAY_EVENTS_CALENDAR_FILENAME)
            .readText(Charsets.UTF_8)
        val inCalendar = Biweekly.parse(calendarString).first()
        val outCalendar = CalendarConverter.convert(inCalendar)
        outCalendar.events.forEach {
            it.dateStart.value.hasTime() shouldBe false
            it.dateEnd.value.hasTime() shouldBe false
        }

        outCalendar.events.size shouldBe inCalendar.events.size
        val firstShift = outCalendar.events.first { it.summary.value == "First Shift" }
        assertDatesMatch(2022, 6, 19, toZonedDate(firstShift.dateStart.value))
        assertDatesMatch(2022, 6, 20, toZonedDate(firstShift.dateEnd.value))
    }

    private fun assertDatesMatch(year: Int, month: Int, day: Int, compareTo: ZonedDateTime) {
        (compareTo == ZonedDateTime.of(
            year,
            month,
            day,
            0,
            0,
            0,
            0,
            ZoneId.systemDefault()
        )) shouldBe true
    }

    private fun toZonedDate(calDate: ICalDate): ZonedDateTime {
        return ZonedDateTime
            .ofInstant(calDate.toInstant(), ZoneId.systemDefault())
    }
}
