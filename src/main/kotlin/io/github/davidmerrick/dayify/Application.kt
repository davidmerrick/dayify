package io.github.davidmerrick.dayify

import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.Micronaut

@Introspected
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .args(*args)
            .packages("io.github.davidmerrick.dayify")
            .mainClass(Application.javaClass)
            .start()
    }
}
