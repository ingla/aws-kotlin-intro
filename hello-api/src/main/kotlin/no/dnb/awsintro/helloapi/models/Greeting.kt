package no.dnb.awsintro.helloapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Greeting(
    val greeting: String
)