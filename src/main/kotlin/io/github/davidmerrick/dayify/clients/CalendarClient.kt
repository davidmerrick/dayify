package io.github.davidmerrick.dayify.clients

import biweekly.Biweekly
import biweekly.ICalendar
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import mu.KotlinLogging
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

@Singleton
class CalendarClient(
    @field:Client private val client: HttpClient
) {
    fun fetchCalendar(url: String): ICalendar {
        log.info("Fetching calendar: $url")
        val request = HttpRequest.GET<String>(url)
            .header(HttpHeaders.USER_AGENT, "Dayify/1.0")
        val responseBody = client
            .toBlocking()
            .retrieve(request)
        return try {
            Biweekly.parse(responseBody).first()
        } catch (e: Exception) {
            log.warn("Failed to parse calendar", e)
            throw e
        }
    }
}
