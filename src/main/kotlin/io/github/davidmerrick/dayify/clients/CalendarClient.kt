package io.github.davidmerrick.dayify.clients

import biweekly.Biweekly
import biweekly.ICalendar
import io.micronaut.http.HttpHeaders.USER_AGENT
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import kotlinx.coroutines.rx2.await
import mu.KotlinLogging
import javax.inject.Singleton

private val log = KotlinLogging.logger {}

private const val USER_AGENT_HEADER = "Dayify/1.0"

@Singleton
class CalendarClient(@param:Client private val client: RxHttpClient) {

    suspend fun fetchCalendar(url: String): ICalendar {
        log.info("Fetching calendar: $url")
        val request = HttpRequest.GET<String>(url)
            .header(USER_AGENT, USER_AGENT_HEADER)
        return client.retrieve(request)
            .firstOrError()
            .await()
            .let { parseCalendar(it) }
    }

    private fun parseCalendar(body: String): ICalendar {
        return try {
            Biweekly.parse(body).first()
        } catch (e: Exception) {
            log.warn("Failed to parse calendar", e)
            throw e
        }
    }
}
