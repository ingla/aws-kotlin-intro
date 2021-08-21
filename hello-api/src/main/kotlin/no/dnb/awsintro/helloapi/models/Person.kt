package no.dnb.awsintro.helloapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    val firstName: String,
    val lastName: String,
    val agYear: String
)