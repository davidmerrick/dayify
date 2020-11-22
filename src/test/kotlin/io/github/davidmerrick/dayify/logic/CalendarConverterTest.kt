package io.github.davidmerrick.dayify.logic

import biweekly.Biweekly
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

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
    }
}
