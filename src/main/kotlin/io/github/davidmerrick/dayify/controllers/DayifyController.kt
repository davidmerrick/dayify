package io.github.davidmerrick.dayify.controllers

import biweekly.Biweekly
import io.github.davidmerrick.dayify.clients.CalendarClient
import io.github.davidmerrick.dayify.logic.CalendarConverter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.util.concurrent.ExecutorService

private const val CAL_MEDIA_TYPE = "text/calendar"

@Controller("/dayify")
class DayifyController(
    private val calendarClient: CalendarClient,
    executors: ExecutorService
) {
    private val coroutineDispatcher = executors.asCoroutineDispatcher()

    /**
     * Converts a calendar to all-day events.
     * @param atZone: Specifies the timezone to use for the calendar. i.e. "America/Los_Angeles".
     * This is important to get the end date correct, as this service strips out the time, which can result
     * in events ending a day later or earlier than expected on the client side.
     */
    @Get("/", produces = [CAL_MEDIA_TYPE])
    suspend fun convertCalendar(
        @QueryValue url: String,
        @QueryValue atZone: ZoneId? = null
    ): HttpResponse<String> = withContext(coroutineDispatcher) {
        val inCalendar = try {
            calendarClient.fetchCalendar(url)
        } catch (e: Exception) {
            return@withContext HttpResponse.badRequest()
        }

        val outCalendar = CalendarConverter.convert(inCalendar, atZone)
        HttpResponse.ok(Biweekly.write(outCalendar).go())
    }
}
