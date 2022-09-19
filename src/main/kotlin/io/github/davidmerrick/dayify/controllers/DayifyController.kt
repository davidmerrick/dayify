package io.github.davidmerrick.dayify.controllers

import biweekly.Biweekly
import io.github.davidmerrick.dayify.clients.CalendarClient
import io.github.davidmerrick.dayify.logic.CalendarConverter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue

private const val CAL_MEDIA_TYPE = "text/calendar"

@Controller("/dayify")
class DayifyController(
    private val calendarClient: CalendarClient
) {

    @Get("/", produces = [CAL_MEDIA_TYPE])
    fun convertCalendar(@QueryValue url: String): HttpResponse<String> {
        val inCalendar = try {
            calendarClient.fetchCalendar(url)
        } catch (e: Exception) {
            return HttpResponse.badRequest()
        }

        val outCalendar = CalendarConverter.convert(inCalendar)
        return HttpResponse.ok(Biweekly.write(outCalendar).go())
    }
}
