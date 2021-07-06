package no.dnb.awsintro.helloapi.utils

import no.dnb.awsintro.helloapi.models.Greeting
import org.http4k.core.Body
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.string

/*
Http4k lenses are entities which helps to set/get properties of a HTTP
message. The properties can be headers, bodies, path parameters etc.
If an HTTP request does not match the specified lens, the framework
will generate a custom response with status '400 Bad Request'.
*/

val namePathParameter = Path.string().of("name")
val greetingBody = Body.auto<Greeting>().toLens()