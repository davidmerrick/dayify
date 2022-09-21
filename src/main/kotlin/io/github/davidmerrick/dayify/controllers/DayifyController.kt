package io.github.davidmerrick.dayify.controllers

import biweekly.Biweekly
import io.github.davidmerrick.dayify.clients.CalendarClient
import io.github.davidmerrick.dayify.logic.CalendarConverter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService

private const val CAL_MEDIA_TYPE = "text/calendar"

@Controller("/dayify")
class DayifyController(
    private val calendarClient: CalendarClient,
    executors: ExecutorService
) {
    private val coroutineDispatcher: CoroutineDispatcher

    init {
        coroutineDispatcher = executors.asCoroutineDispatcher()
    }

    @Get("/", produces = [CAL_MEDIA_TYPE])
    suspend fun convertCalendar(@QueryValue url: String): HttpResponse<String> = withContext(coroutineDispatcher) {
        val inCalendar = try {
            calendarClient.fetchCalendar(url)
        } catch (e: Exception) {
            return@withContext HttpResponse.badRequest()
        }

        val outCalendar = CalendarConverter.convert(inCalendar)
        HttpResponse.ok(Biweekly.write(outCalendar).go())
    }
}
