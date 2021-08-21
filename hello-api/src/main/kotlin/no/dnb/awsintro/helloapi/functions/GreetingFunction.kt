package no.dnb.awsintro.helloapi.functions

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import no.dnb.awsintro.helloapi.dynamodb.StringItem
import no.dnb.awsintro.helloapi.models.Greeting
import no.dnb.awsintro.helloapi.utils.greetingBody
import no.dnb.awsintro.helloapi.utils.namePathParameter
import no.dnb.awsintro.helloapi.utils.stringBody
import no.dnb.awsintro.helloapi.utils.stringKeyPathParameter
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

    private val mapper: DynamoDBMapper

    init {
        val client = AmazonDynamoDBClientBuilder
            .standard()
            .build()
        mapper = DynamoDBMapper(client)
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

    val postString: HttpHandler = { request ->
        // Extract request body
        val newStringEntry = stringBody(request)
        if (newStringEntry.stringKey.isBlank()) {
            Response(Status.BAD_REQUEST)
        }

        mapper.save(newStringEntry)
        Response(Status.CREATED)
    }

    val getString: HttpHandler = { request ->
        // Extract path parameter from request
        val stringKey = stringKeyPathParameter(request)
        val stringEntry: StringItem? = mapper.load(StringItem::class.java, stringKey)

        if (stringEntry == null) {
            Response(Status.NOT_FOUND)
        } else {
            Response(Status.OK)
                .with(
                    stringBody of stringEntry
                )
        }
    }

    val routes: RoutingHttpHandler = routes(
        "v1/greeting/{name}" bind Method.GET to getGreeting,
        "v1/string-catalog/" bind Method.POST to postString,
        "v1/string-catalog/{stringKey}" bind Method.GET to getString
    )

    val service = DebuggingFilters.PrintRequestAndResponse()
        .then(ServerFilters.CatchLensFailure()) // Will catch lens validation errors
        .then(routes)
}
