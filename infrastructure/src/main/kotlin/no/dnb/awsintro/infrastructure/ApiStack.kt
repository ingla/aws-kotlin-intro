package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.core.Aspects
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.Environment
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.core.StackProps
import software.amazon.awscdk.services.apigateway.EndpointType
import software.amazon.awscdk.services.apigateway.IRestApi
import software.amazon.awscdk.services.apigateway.LogGroupLogDestination
import software.amazon.awscdk.services.apigateway.MethodLoggingLevel
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.apigateway.StageOptions
import software.amazon.awscdk.services.iam.IManagedPolicy
import software.amazon.awscdk.services.iam.IRole
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.Role
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.IFunction
import software.amazon.awscdk.services.lambda.Runtime.JAVA_11
import software.amazon.awscdk.services.logs.LogGroup
import software.amazon.awscdk.services.logs.RetentionDays

class ApiStack private constructor(
    scope: Construct,
    id: String,
    private val props: Props,
    stackProps: StackProps,
) : Stack(scope, id, stackProps) {

    data class Props(
        val environment: String,
        val label: String?,
        val namespace: Namespace,
        val baseName: String,
        val account: String,
        val region: String
    )

    companion object {
        fun build(scope: Construct, id: String, props: Props): ApiStack {
            val stackProps = StackProps.builder()
                .stackName(props.namespace.prefixWithNamespace("greeting-api"))
                .description("Contains resources for a simple API")
                .env(
                    Environment.builder()
                        .account(props.account)
                        .region(props.region)
                        .build()
                )
                .build()
            return ApiStack(scope, id, props, stackProps)
        }
    }

    private val restApi: IRestApi
    private val greetingLambda: IFunction
    private val lambdaRole: IRole
    //private val dynamoDBTable: ITable

    init {
        restApi = makeRestApi()

        lambdaRole = makeLambdaRole()

        greetingLambda = makeLambda(
            logicalId = "GetGreetingFunction",
            baseName = "get-greeting-v1",
            handler = "no.dnb.awsintro.helloapi.functions.GreeterFunctionHandler",
            role = lambdaRole
        )

        restApi.addLambdaIntegration(
            "GET",
            "/v1/greeting/{name}",
            greetingLambda
        )

        /* Remember that all resources you make should be tagged with your initials. */
        //dynamoDBTable = makeTable()
        //dynamoDBTable.grantReadWriteData(lambdaRole)

        Aspects.of(this).add(PermissionBoundaryChecker())
    }

    private fun makeRestApi(): RestApi {
        val accessLogGroupLogicalId = "ApiAccessLogGroup"
        val accessLogGroup = LogGroup.Builder
            .create(this, accessLogGroupLogicalId)
            .retention(RetentionDays.ONE_DAY)
            .build()

        accessLogGroup.overrideLogicalId(accessLogGroupLogicalId)

        val stageOptions = StageOptions.builder()
            .stageName(props.environment)
            .metricsEnabled(true)
            .loggingLevel(MethodLoggingLevel.ERROR)
            .accessLogDestination(LogGroupLogDestination(accessLogGroup))
            .build()

        val logicalId = "GreetingAPI"
        val api = RestApi.Builder
            .create(this, logicalId)
            .restApiName(props.namespace.postfixWithNamespace("greeting"))
            .description("API for getting a greeting")
            .deployOptions(stageOptions)
            .endpointConfiguration { listOf(EndpointType.REGIONAL) }
            .build()

        api.overrideLogicalId(logicalId)
        return api
    }

    private fun makeLambda(
        role: IRole,
        logicalId: String,
        baseName: String,
        handler: String?
    ): IFunction {
        val envVariables = getLambdaEnvVars()

        val function = Function.Builder
            .create(this, logicalId)
            .functionName(props.namespace.prefixWithNamespace(baseName))
            .runtime(JAVA_11)
            .code(Code.fromAsset("hello-api/build/libs/production-aws.jar"))
            .handler(handler)
            .environment(envVariables)
            .memorySize(4096)
            .timeout(Duration.seconds(30))
            .role(role)
            .logRetention(RetentionDays.ONE_DAY)
            .build()

        function.overrideLogicalId(logicalId)

        return function
    }

    private fun getManagedPolicies(): List<IManagedPolicy> {
        return listOf(
            ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
            ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaVPCAccessExecutionRole")
        )
    }

    private fun makeLambdaRole(): IRole {
        return Role.Builder
            .create(this, "LambdaRole")
            .assumedBy(ServicePrincipal("lambda.amazonaws.com"))
            .roleName(props.namespace.prefixWithNamespace("role"))
            .description("Role for greeting lambda")
            .managedPolicies(getManagedPolicies())
            .build()
    }

    private fun getLambdaEnvVars(): Map<String, String> {
        return mapOf(
            "ENVIRONMENT" to props.environment,
        )
    }
}