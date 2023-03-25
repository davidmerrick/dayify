package io.github.davidmerrick.dayify.logic

import biweekly.Biweekly
import biweekly.ICalendar
import biweekly.util.ICalDate
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

private const val ON_CALL_CALENDAR_FILENAME = "/on_call_schedule.ics"
private const val SINGLE_DAY_EVENTS_CALENDAR_FILENAME = "/single_day_events.ics"

private const val PDT_ZONE = "America/Los_Angeles"

class CalendarConverterTest {
    @Test
    fun `Parse calendar and convert to all-day events`() {
        val inCalendar = parseCalendar(ON_CALL_CALENDAR_FILENAME)
        with(CalendarConverter.convert(inCalendar)) {
            events.forEach {
                it.dateStart.value.hasTime() shouldBe false
                it.dateEnd.value.hasTime() shouldBe false
            }

            events.size shouldBe inCalendar.events.size
            val fooEvent = events.first { it.summary.value == "Foo event" }
            assertDatesMatch(2020, 11, 4, toZonedDate(fooEvent.dateStart.value))
            assertDatesMatch(2020, 11, 11, toZonedDate(fooEvent.dateEnd.value))
        }
    }

    @Test
    fun `Parse webcal calendar with single-day events and convert to all-day events`() {
        val inCalendar = parseCalendar(SINGLE_DAY_EVENTS_CALENDAR_FILENAME)
        with(CalendarConverter.convert(inCalendar)) {
            events.forEach {
                it.dateStart.value.hasTime() shouldBe false
                it.dateEnd.value.hasTime() shouldBe false
            }

            events.size shouldBe inCalendar.events.size
            val firstShift = events.first { it.summary.value == "First Shift" }
            assertDatesMatch(2023, 4, 21, toZonedDate(firstShift.dateStart.value))
            assertDatesMatch(2023, 4, 22, toZonedDate(firstShift.dateEnd.value))
        }
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
            ZoneId.of(PDT_ZONE)
        )) shouldBe true
    }

    private fun parseCalendar(fileName: String): ICalendar {
        val calendarString = this::class.java.getResource(fileName)
            .readText(Charsets.UTF_8)
        return Biweekly.parse(calendarString).first()
    }

    private fun toZonedDate(calDate: ICalDate) = toZonedDate(calDate, ZoneId.of(PDT_ZONE))

    private fun toZonedDate(calDate: ICalDate, zone: ZoneId): ZonedDateTime {
        return ZonedDateTime.ofInstant(
            calDate.toInstant(),
            zone
        )
    }
}
