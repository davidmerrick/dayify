package io.github.davidmerrick.dayify.clients

import biweekly.Biweekly
import biweekly.ICalendar
import io.micronaut.http.HttpHeaders.USER_AGENT
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.util.concurrent.ExecutorService
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

private val log = KotlinLogging.logger {}

private const val USER_AGENT_HEADER = "Dayify/1.0"

@Singleton
class CalendarClient(@param:Client private val client: RxHttpClient) {

    suspend fun fetchCalendar(url: String): ICalendar {
        log.info("Fetching calendar: $url")
        val request = HttpRequest.GET<String>(url)
            .header(USER_AGENT, USER_AGENT_HEADER)
        val responseBody = client.retrieve(request)
            .firstOrError()
            .toFuture()
            .get()

        return try {
            Biweekly.parse(responseBody).first()
        } catch (e: Exception) {
            log.warn("Failed to parse calendar", e)
            throw e
        }
    }
}
