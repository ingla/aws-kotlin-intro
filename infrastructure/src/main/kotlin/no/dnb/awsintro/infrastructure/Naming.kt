package no.dnb.awsintro.infrastructure

import software.amazon.awscdk.core.CfnResource
import software.amazon.awscdk.core.IConstruct

fun IConstruct.overrideLogicalId(logicalId: String) {
    (this.node.defaultChild as CfnResource).overrideLogicalId(logicalId)
}