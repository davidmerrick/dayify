package io.github.davidmerrick.dayify.controllers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import io.kotest.common.runBlocking
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val DAYIFY_PATH = "/dayify"

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DayifyControllerTest {

    private val wireMockServer = WireMockServer()

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeAll
    fun start() {
        wireMockServer.start()
    }

    @AfterEach
    fun reset() {
        wireMockServer.resetAll()
    }

    @Test
    fun `Critical path test`() = runBlocking {
        // Set up the mocks
        val calendarPath = "/external-calendars/" + RandomStringUtils.randomAlphanumeric(10)
        mockCalendarResponse(calendarPath, "/on_call_schedule.ics")

        val request = HttpRequest.GET<String>(DAYIFY_PATH)
            .accept("text/calendar")
            .apply {
                parameters.add("url", wireMockServer.baseUrl() + calendarPath)
            }

        client.toBlocking().retrieve(request)

        wireMockServer.verify(
            1,
            getRequestedFor(
                urlPathMatching(calendarPath)
            )
        )
    }

    private fun mockCalendarResponse(path: String, bodyFile: String) {
        val calendarString = this::class.java.getResource(bodyFile)
            .readText(Charsets.UTF_8)
        val responseDefinitionBuilder = WireMock.aResponse()
            .withStatus(HttpStatus.OK.code)
            .withBody(calendarString)

        wireMockServer.stubFor(
            WireMock
                .get(path)
                .willReturn(responseDefinitionBuilder)
        )
    }
}
