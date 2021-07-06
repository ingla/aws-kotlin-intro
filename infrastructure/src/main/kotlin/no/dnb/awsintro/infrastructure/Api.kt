package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.services.apigateway.IResource
import software.amazon.awscdk.services.apigateway.IRestApi
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.lambda.IFunction

fun IRestApi.addLambdaIntegration(
        method: String,
        path: String,
        lambda: IFunction
) {
    val resource = getResourceByPath(path)
    val integration = LambdaIntegration.Builder
            .create(lambda)
            .build()

    resource.addMethod(
            method,
            integration
    )
}

fun IRestApi.getResourceByPath(path: String): IResource {
    // Recursively add each path parts as resource
    return path
            .split("/")
            .fold(root) { parent, pathPart ->
                if (pathPart.isBlank()) {
                    parent
                } else {
                    val child = parent.getResource(pathPart)
                    child ?: parent.addResource(pathPart)
                }
            }
}