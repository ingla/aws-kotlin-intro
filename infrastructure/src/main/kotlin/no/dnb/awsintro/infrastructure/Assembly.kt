package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.core.App
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.cxapi.CloudAssembly

class Assembly {
    fun synthesize(app: App): CloudAssembly {
        return app.run {
            val apiProps = getApiStackProps(this)
            ApiStack.build(this, "ApiStack", apiProps)

            synth()
        }
    }

    private fun getApiStackProps(
            scope: Construct,
    ): ApiStack.Props {
        // These values are hardcoded for now, but should be dynamic in real-life.
        val env = "sand"   // Sandbox
        val label = "IL18" // TODO: Set to <Initials><AG start year>. All deployed resources in AWS will have this label in their name.
        val baseName = "greeting"
        val account = "" // AWS account number
        val region = "eu-west-1"     // AWS region
        val namespace = Namespace(env, label)

        return ApiStack.Props(
                environment = env,
                label = label,
                namespace = namespace,
                baseName = baseName,
                account = account,
                region = region
        )
    }
}