package no.dnb.awsintro.helloapi.functions

import org.http4k.client.ApacheClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.serverless.ApiGatewayRestLambdaFunction
import org.http4k.serverless.AppLoader
import org.slf4j.LoggerFactory

object PersonLambda: AppLoader {
    private val personService = PersonService()
    override fun invoke(environment: Map<String, String>): HttpHandler {
        return personService.service
    }
}

// Entry point for AWS lambda
class PersonFunctionHandler: ApiGatewayRestLambdaFunction(PersonLambda)

class PersonService {
    companion object {
        private val logger = LoggerFactory.getLogger(GreetingService::class.java)
    }

    val getPerson: HttpHandler = { request ->
        val client = ApacheClient()
        val stringRequest = Request(Method.GET,
            // Replace with the trigger endpoint from the Apigateway
            "https://c2ll49tamg.execute-api.eu-west-1.amazonaws.com/sand/v1/string-catalog/name")
        client(stringRequest)
    }

    val routes: RoutingHttpHandler = routes(
        "v1/person" bind Method.GET to getPerson,
    )

    val service = DebuggingFilters.PrintRequestAndResponse()
        .then(ServerFilters.CatchLensFailure()) // Will catch lens validation errors
        .then(routes)
}