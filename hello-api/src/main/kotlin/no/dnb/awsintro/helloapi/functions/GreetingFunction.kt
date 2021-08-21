package no.dnb.awsintro.helloapi.functions

import no.dnb.awsintro.helloapi.models.Greeting
import no.dnb.awsintro.helloapi.utils.greetingBody
import no.dnb.awsintro.helloapi.utils.namePathParameter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.serverless.ApiGatewayRestLambdaFunction
import org.http4k.serverless.AppLoader
import org.slf4j.LoggerFactory

// This AppLoader is responsible for building our HttpHandler which is supplied to AWS
object GreeterLambda: AppLoader {
    private val greetingService = GreetingService()
    override fun invoke(environment: Map<String, String>): HttpHandler {
        return greetingService.service
    }
}

// Entry point for AWS lambda
class GreeterFunctionHandler: ApiGatewayRestLambdaFunction(GreeterLambda)

class GreetingService {
    companion object {
        private val logger = LoggerFactory.getLogger(GreetingService::class.java)
    }

    val getGreeting: HttpHandler = { request ->
        // Extract path parameter from request using 'lenses'. See no.dnb.awsintro.helloapi.utils/Lenses.kt
        val name = namePathParameter(request)
        val greeting = Greeting(greeting = "Hello $name")

        // Return a HTTP response
        Response(Status.OK)
            .with(
                greetingBody of greeting
            )
    }

    val routes: RoutingHttpHandler = routes(
        "v1/greeting/{name}" bind Method.GET to getGreeting
    )

    val service = DebuggingFilters.PrintRequestAndResponse()
        .then(ServerFilters.CatchLensFailure()) // Will catch lens validation errors
        .then(routes)
}
