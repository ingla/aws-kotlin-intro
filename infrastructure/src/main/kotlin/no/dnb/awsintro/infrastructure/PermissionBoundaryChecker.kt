package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.IAspect
import software.amazon.awscdk.core.IConstruct
import software.amazon.awscdk.services.iam.CfnRole
import software.amazon.awscdk.services.iam.IManagedPolicy
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.Role

// This Aspect would default the permission boundary of roles (if they don't have one)
// Read more about aspects here: https://docs.aws.amazon.com/cdk/latest/guide/aspects.html
class PermissionBoundaryChecker : IAspect {
    companion object {
        fun defaultBoundary(scope: Construct): IManagedPolicy {
            return ManagedPolicy.fromManagedPolicyName(
                scope,
                "CoreBoundary",
                "Core-PermissionBoundaryPolicy"
            )
        }
    }
    override fun visit(node: IConstruct) {
        if (node is Role) {
                val permissionBoundary = node.permissionsBoundary
            if (permissionBoundary == null) {
                val cfnRole = node.node.defaultChild as CfnRole
                val defaultBoundaryArn = defaultBoundary(node).managedPolicyArn
                println("Found role without permission boundary. Attaching default boundary $defaultBoundaryArn")
                cfnRole.permissionsBoundary = defaultBoundaryArn
            }
        }
    }
}