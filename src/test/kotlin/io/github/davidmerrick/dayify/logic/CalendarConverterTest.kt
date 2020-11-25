package io.github.davidmerrick.dayify.logic

import biweekly.Biweekly
import biweekly.property.DateEnd
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

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
        toLocalDate(fooEvent.dateEnd) shouldBe LocalDate.of(2020, 11, 12)
    }

    private fun toLocalDate(dateEnd: DateEnd): LocalDate {
        val dateInstant = dateEnd.value.toInstant()
        return LocalDate.ofInstant(dateInstant, ZoneId.systemDefault())
    }
}
