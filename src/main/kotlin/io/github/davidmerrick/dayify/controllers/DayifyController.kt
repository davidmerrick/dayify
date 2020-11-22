package io.github.davidmerrick.dayify.controllers

import biweekly.Biweekly
import io.github.davidmerrick.dayify.logic.CalendarConverter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

private const val CAL_MEDIA_TYPE = "text/calendar"

@Controller("/dayify")
class DayifyController(@Client private val client: HttpClient) {

    @Get("/", produces = [CAL_MEDIA_TYPE])
    fun dayify(@QueryValue url: String): HttpResponse<String> {
        log.info("Fetching calendar: $url")
        val responseBody = client.toBlocking()
            .exchange(url, String::class.java)
            .body()

        val inCalendar = try {
            Biweekly.parse(responseBody).first()
        } catch (e: Exception) {
            log.warn("Failed to parse calendar", e)
            return HttpResponse.badRequest()
        }

        val outCalendar = CalendarConverter.convert(inCalendar)
        return HttpResponse.ok(Biweekly.write(outCalendar).go())
    }
}
