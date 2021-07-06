package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.core.App

object MainApp {
    @JvmStatic
    fun main(argv: Array<String>) {
        val app = App()
        Assembly().synthesize(app)
    }
}