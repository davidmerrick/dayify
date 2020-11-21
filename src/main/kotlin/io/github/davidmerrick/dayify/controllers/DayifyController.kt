package io.github.davidmerrick.dayify.controllers

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

@Controller("/dayify")
class DayifyController {

    @Get("/", produces = [MediaType.TEXT_PLAIN])
    fun dayify(@QueryValue url: String): String? {
        log.info("Fetching calendar: $url")
        return "Success"
    }
}
