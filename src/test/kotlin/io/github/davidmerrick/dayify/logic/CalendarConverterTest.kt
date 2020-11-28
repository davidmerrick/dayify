package io.github.davidmerrick.dayify.logic

import biweekly.Biweekly
import biweekly.property.DateEnd
import biweekly.util.ICalDate
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

private const val TEST_CALENDAR_FILENAME = "/on_call_schedule.ics"

class CalendarConverterTest {

    @Test
    fun `Parse calendar and convert to all-day events`() {
        val calendarString = this::class.java.getResource(TEST_CALENDAR_FILENAME)
            .readText(Charsets.UTF_8)
        val inCalendar = Biweekly.parse(calendarString).first()
        val outCalendar = CalendarConverter.convert(inCalendar)
        outCalendar.events.forEach {
            it.dateStart.value.hasTime() shouldBe false
            it.dateEnd.value.hasTime() shouldBe false
        }

        outCalendar.events.size shouldBe inCalendar.events.size
        val fooEvent = outCalendar.events.first { it.summary.value == "Foo event" }
        toLocalDate(fooEvent.dateStart.value) shouldBe LocalDate.of(2020, 11, 12)
        toLocalDate(fooEvent.dateEnd.value) shouldBe LocalDate.of(2020, 11, 12)
    }

    private fun toLocalDate(calDate: ICalDate): LocalDate {
        val dateInstant = calDate.toInstant()
        return LocalDateTime.ofInstant(dateInstant, ZoneId.ofOffset("UTC", ZoneOffset.UTC))
            .toLocalDate()
    }
}
